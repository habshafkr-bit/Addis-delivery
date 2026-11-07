package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.CartItem
import com.example.ui.components.CartSummaryBar
import com.example.ui.components.MarketTopBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CheckoutConfirmDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderSuccessDialog
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailSheet
import com.example.ui.theme.MarketGreenDark
import com.example.ui.theme.MarketGreenPrimary
import com.example.ui.theme.MiniMarketTheme
import com.example.viewmodel.MarketTab
import com.example.viewmodel.MarketViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MarketViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MiniMarketTheme {
                MarketApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MarketApp(viewModel: MarketViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val orderHistory by viewModel.orderHistory.collectAsStateWithLifecycle()

    val totalCartItems = cartItems.sumOf { it.quantity }
    val cartSubtotal = cartItems.sumOf { it.totalPrice }
    val cartDiscount = cartSubtotal * uiState.discountPercent
    val cartDeliveryFee = if (cartSubtotal >= 50.0 || cartSubtotal == 0.0) 0.0 else 5.0
    val cartVat = (cartSubtotal - cartDiscount) * 0.15
    val cartGrandTotal = (cartSubtotal - cartDiscount) + cartDeliveryFee + cartVat

    // Handle Back Button navigation
    if (uiState.currentTab != MarketTab.SHOP) {
        BackHandler {
            viewModel.onTabSelected(MarketTab.SHOP)
        }
    }

    Scaffold(
        topBar = {
            MarketTopBar(
                cartCount = totalCartItems,
                onCartClick = { viewModel.onTabSelected(MarketTab.CART) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == MarketTab.SHOP,
                    onClick = { viewModel.onTabSelected(MarketTab.SHOP) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == MarketTab.SHOP) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                            contentDescription = "Shop"
                        )
                    },
                    label = { Text("Shop", fontWeight = if (uiState.currentTab == MarketTab.SHOP) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MarketGreenDark,
                        selectedTextColor = MarketGreenDark,
                        indicatorColor = Color(0xFFD3F2DF)
                    ),
                    modifier = Modifier.testTag("nav_tab_shop")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == MarketTab.CART,
                    onClick = { viewModel.onTabSelected(MarketTab.CART) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (totalCartItems > 0) {
                                    Badge(
                                        containerColor = Color(0xFFF59E0B),
                                        contentColor = Color.Black
                                    ) {
                                        Text(
                                            text = if (totalCartItems > 99) "99+" else totalCartItems.toString(),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.currentTab == MarketTab.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    },
                    label = { Text("Cart", fontWeight = if (uiState.currentTab == MarketTab.CART) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MarketGreenDark,
                        selectedTextColor = MarketGreenDark,
                        indicatorColor = Color(0xFFD3F2DF)
                    ),
                    modifier = Modifier.testTag("nav_tab_cart")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == MarketTab.ORDERS,
                    onClick = { viewModel.onTabSelected(MarketTab.ORDERS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == MarketTab.ORDERS) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "Orders"
                        )
                    },
                    label = { Text("Orders", fontWeight = if (uiState.currentTab == MarketTab.ORDERS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MarketGreenDark,
                        selectedTextColor = MarketGreenDark,
                        indicatorColor = Color(0xFFD3F2DF)
                    ),
                    modifier = Modifier.testTag("nav_tab_orders")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                MarketTab.SHOP -> {
                    HomeScreen(
                        products = filteredProducts,
                        cartItems = cartItems,
                        favoriteIds = favoriteIds,
                        searchQuery = uiState.searchQuery,
                        selectedCategory = uiState.selectedCategory,
                        onSearchQueryChange = viewModel::onSearchQueryChanged,
                        onCategorySelected = viewModel::onCategorySelected,
                        onAddToCart = viewModel::addToCart,
                        onDecrement = viewModel::decrementFromCart,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onProductClick = viewModel::openProductDetail
                    )

                    // Sticky quick cart bar when on shop screen and cart has items
                    CartSummaryBar(
                        itemCount = totalCartItems,
                        totalPrice = cartGrandTotal,
                        onViewCartClick = { viewModel.onTabSelected(MarketTab.CART) },
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                MarketTab.CART -> {
                    CartScreen(
                        cartItems = cartItems,
                        promoCode = uiState.promoCode,
                        promoSuccess = uiState.promoCodeSuccess,
                        promoError = uiState.promoCodeError,
                        discountPercent = uiState.discountPercent,
                        deliveryAddress = uiState.deliveryAddress,
                        paymentMethod = uiState.paymentMethod,
                        onAddToCart = viewModel::addToCart,
                        onDecrement = viewModel::decrementFromCart,
                        onRemoveItem = viewModel::removeFromCart,
                        onClearCart = viewModel::clearCart,
                        onPromoCodeChange = viewModel::onPromoCodeChanged,
                        onApplyPromo = viewModel::applyPromoCode,
                        onAddressChange = viewModel::onDeliveryAddressChanged,
                        onPaymentMethodChange = viewModel::onPaymentMethodChanged,
                        onCheckoutClick = viewModel::openCheckoutDialog,
                        onExploreShop = { viewModel.onTabSelected(MarketTab.SHOP) }
                    )
                }

                MarketTab.ORDERS -> {
                    OrdersScreen(
                        orders = orderHistory,
                        onReorder = viewModel::reorder,
                        onStartShopping = { viewModel.onTabSelected(MarketTab.SHOP) }
                    )
                }
            }

            // Product Detail Modal Sheet
            uiState.selectedProductForDetail?.let { product ->
                val currentQty = cartItems.find { it.product.id == product.id }?.quantity ?: 0
                ProductDetailSheet(
                    product = product,
                    cartQuantity = currentQty,
                    isFavorite = favoriteIds.contains(product.id),
                    onAddToCart = { viewModel.addToCart(product) },
                    onToggleFavorite = { viewModel.toggleFavorite(product.id) },
                    onDismiss = viewModel::dismissProductDetail
                )
            }

            // Checkout Confirmation Dialog
            if (uiState.isCheckoutDialogVisible) {
                CheckoutConfirmDialog(
                    cartItems = cartItems,
                    subtotal = cartSubtotal,
                    discount = cartDiscount,
                    deliveryFee = cartDeliveryFee,
                    vat = cartVat,
                    total = cartGrandTotal,
                    deliveryAddress = uiState.deliveryAddress,
                    paymentMethod = uiState.paymentMethod,
                    onConfirm = viewModel::confirmOrder,
                    onDismiss = viewModel::dismissCheckoutDialog
                )
            }

            // Order Success Dialog
            if (uiState.showOrderSuccessDialog && uiState.lastCompletedOrder != null) {
                OrderSuccessDialog(
                    order = uiState.lastCompletedOrder!!,
                    onViewOrders = {
                        viewModel.dismissOrderSuccessDialog()
                        viewModel.onTabSelected(MarketTab.ORDERS)
                    },
                    onDismiss = {
                        viewModel.dismissOrderSuccessDialog()
                        viewModel.onTabSelected(MarketTab.SHOP)
                    }
                )
            }
        }
    }
}
