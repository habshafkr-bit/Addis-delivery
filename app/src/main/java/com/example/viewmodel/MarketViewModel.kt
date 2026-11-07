package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MarketRepository
import com.example.model.CartItem
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class MarketUiState(
    val searchQuery: String = "",
    val selectedCategory: ProductCategory = ProductCategory.ALL,
    val currentTab: MarketTab = MarketTab.SHOP,
    val selectedProductForDetail: Product? = null,
    val isCheckoutDialogVisible: Boolean = false,
    val lastCompletedOrder: Order? = null,
    val promoCode: String = "",
    val promoCodeError: String? = null,
    val promoCodeSuccess: String? = null,
    val discountPercent: Double = 0.0,
    val deliveryAddress: String = "King Fahd Rd, Riyadh, Saudi Arabia",
    val paymentMethod: PaymentMethod = PaymentMethod.MADA,
    val deliveryNote: String = "Leave at front door",
    val showOrderSuccessDialog: Boolean = false
)

enum class MarketTab(val label: String) {
    SHOP("Shop"),
    CART("Cart"),
    ORDERS("Orders")
}

class MarketViewModel(
    private val repository: MarketRepository = MarketRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketUiState())
    val uiState: StateFlow<MarketUiState> = _uiState.asStateFlow()

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val favoriteIds: StateFlow<Set<Int>> = repository.favoriteIds
    val orderHistory: StateFlow<List<Order>> = repository.orders

    val filteredProducts: StateFlow<List<Product>> = combine(
        _uiState,
        favoriteIds
    ) { state, _ ->
        val query = state.searchQuery.trim().lowercase()
        val category = state.selectedCategory

        repository.catalogProducts.filter { product ->
            val matchesCategory = category == ProductCategory.ALL || product.category == category
            val matchesSearch = query.isEmpty() ||
                    product.name.lowercase().contains(query) ||
                    product.emoji.contains(query) ||
                    product.description.lowercase().contains(query) ||
                    product.category.displayName.lowercase().contains(query)

            matchesCategory && matchesSearch
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = repository.catalogProducts
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: ProductCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onTabSelected(tab: MarketTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun openProductDetail(product: Product) {
        _uiState.update { it.copy(selectedProductForDetail = product) }
    }

    fun dismissProductDetail() {
        _uiState.update { it.copy(selectedProductForDetail = null) }
    }

    fun addToCart(product: Product) {
        repository.addToCart(product, 1)
    }

    fun decrementFromCart(product: Product) {
        repository.addToCart(product, -1)
    }

    fun removeFromCart(productId: Int) {
        repository.removeFromCart(productId)
    }

    fun clearCart() {
        repository.clearCart()
    }

    fun toggleFavorite(productId: Int) {
        repository.toggleFavorite(productId)
    }

    fun onPromoCodeChanged(code: String) {
        _uiState.update { it.copy(promoCode = code, promoCodeError = null, promoCodeSuccess = null) }
    }

    fun applyPromoCode() {
        val code = _uiState.value.promoCode.trim().uppercase()
        when (code) {
            "MINI10" -> {
                _uiState.update {
                    it.copy(
                        discountPercent = 0.10,
                        promoCodeSuccess = "10% off discount applied!",
                        promoCodeError = null
                    )
                }
            }
            "WELCOME15" -> {
                _uiState.update {
                    it.copy(
                        discountPercent = 0.15,
                        promoCodeSuccess = "15% off welcome voucher applied!",
                        promoCodeError = null
                    )
                }
            }
            "" -> {
                _uiState.update {
                    it.copy(
                        promoCodeError = "Please enter a code",
                        promoCodeSuccess = null
                    )
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        promoCodeError = "Invalid code. Try 'MINI10' or 'WELCOME15'",
                        promoCodeSuccess = null
                    )
                }
            }
        }
    }

    fun onDeliveryAddressChanged(address: String) {
        _uiState.update { it.copy(deliveryAddress = address) }
    }

    fun onDeliveryNoteChanged(note: String) {
        _uiState.update { it.copy(deliveryNote = note) }
    }

    fun onPaymentMethodChanged(method: PaymentMethod) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun openCheckoutDialog() {
        if (cartItems.value.isNotEmpty()) {
            _uiState.update { it.copy(isCheckoutDialogVisible = true) }
        }
    }

    fun dismissCheckoutDialog() {
        _uiState.update { it.copy(isCheckoutDialogVisible = false) }
    }

    fun confirmOrder() {
        val currentCart = cartItems.value
        if (currentCart.isEmpty()) return

        val subtotal = currentCart.sumOf { it.totalPrice }
        val discount = subtotal * _uiState.value.discountPercent
        val deliveryFee = if (subtotal >= 50.0) 0.0 else 5.0
        val vat = (subtotal - discount) * 0.15
        val grandTotal = (subtotal - discount) + deliveryFee + vat

        val order = Order(
            id = "MMM-" + (1000 + Random.nextInt(9000)),
            timestamp = System.currentTimeMillis(),
            items = currentCart,
            subtotal = subtotal,
            discount = discount,
            deliveryFee = deliveryFee,
            vat = vat,
            total = grandTotal,
            deliveryAddress = _uiState.value.deliveryAddress,
            paymentMethod = _uiState.value.paymentMethod
        )

        repository.addOrder(order)
        repository.clearCart()

        _uiState.update {
            it.copy(
                isCheckoutDialogVisible = false,
                lastCompletedOrder = order,
                showOrderSuccessDialog = true,
                promoCode = "",
                discountPercent = 0.0,
                promoCodeSuccess = null,
                promoCodeError = null
            )
        }
    }

    fun dismissOrderSuccessDialog() {
        _uiState.update { it.copy(showOrderSuccessDialog = false) }
    }

    fun reorder(order: Order) {
        order.items.forEach { item ->
            repository.addToCart(item.product, item.quantity)
        }
        _uiState.update { it.copy(currentTab = MarketTab.CART) }
    }
}
