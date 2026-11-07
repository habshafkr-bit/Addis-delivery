package com.example.data

import com.example.model.CartItem
import com.example.model.Order
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.model.ProductCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MarketRepository {

    val catalogProducts: List<Product> = listOf(
        Product(
            id = 1,
            name = "Milk",
            price = 6.00,
            emoji = "🥛",
            category = ProductCategory.DAIRY,
            unit = "1 Liter",
            description = "Fresh whole cow's milk rich in calcium, protein, and vitamins. Farm pasteurized.",
            badge = "Essential",
            calories = 149
        ),
        Product(
            id = 2,
            name = "Bread",
            price = 3.00,
            emoji = "🍞",
            category = ProductCategory.BAKERY,
            unit = "400g Loaf",
            description = "Soft oven-baked white sliced sandwich bread. Baked fresh every morning.",
            badge = "Daily Fresh",
            calories = 265
        ),
        Product(
            id = 3,
            name = "Apple",
            price = 2.00,
            emoji = "🍎",
            category = ProductCategory.FRUITS,
            unit = "1 piece",
            description = "Crisp, sweet, and juicy red Gala apples handpicked from premium orchards.",
            badge = "Organic",
            calories = 95
        ),
        Product(
            id = 4,
            name = "Banana",
            price = 4.00,
            emoji = "🍌",
            category = ProductCategory.FRUITS,
            unit = "1 bunch (500g)",
            description = "Naturally ripened golden Cavendish bananas. Ideal natural energy snack.",
            calories = 105
        ),
        Product(
            id = 5,
            name = "Water",
            price = 1.00,
            emoji = "💧",
            category = ProductCategory.BEVERAGES,
            unit = "500 ml",
            description = "Pure mountain spring bottled water, refreshing, ultra-clean, and mineral-balanced.",
            badge = "Best Seller",
            calories = 0
        ),
        Product(
            id = 6,
            name = "Juice",
            price = 5.00,
            emoji = "🧃",
            category = ProductCategory.BEVERAGES,
            unit = "330 ml",
            description = "100% natural pressed mixed orange, apple, and passion fruit juice with Vitamin C.",
            calories = 120
        ),
        Product(
            id = 7,
            name = "Eggs",
            price = 12.00,
            emoji = "🥚",
            category = ProductCategory.DAIRY,
            unit = "12 pack",
            description = "Farm-fresh grade A brown eggs. High quality protein for breakfast and baking.",
            badge = "Farm Fresh",
            calories = 78
        ),
        Product(
            id = 8,
            name = "Chocolate",
            price = 4.00,
            emoji = "🍫",
            category = ProductCategory.SNACKS,
            unit = "90g Bar",
            description = "Decadent and smooth milk chocolate bar crafted with creamy Swiss cocoa butter.",
            badge = "Popular",
            calories = 210
        ),
        Product(
            id = 9,
            name = "Croissant",
            price = 4.50,
            emoji = "🥐",
            category = ProductCategory.BAKERY,
            unit = "1 piece",
            description = "Flaky golden French butter croissant with light crispy layers and airy crumb.",
            badge = "Freshly Baked",
            calories = 230
        ),
        Product(
            id = 10,
            name = "Fresh Cheese",
            price = 8.50,
            emoji = "🧀",
            category = ProductCategory.DAIRY,
            unit = "250g Block",
            description = "Authentic Mediterranean white feta cheese in brine, creamy and tangy.",
            calories = 260
        ),
        Product(
            id = 11,
            name = "Orange",
            price = 3.50,
            emoji = "🍊",
            category = ProductCategory.FRUITS,
            unit = "500g Bag",
            description = "Juicy and vibrant Valencia sweet oranges, perfect for eating or slicing.",
            calories = 62
        ),
        Product(
            id = 12,
            name = "Ice Cream",
            price = 7.00,
            emoji = "🍦",
            category = ProductCategory.SNACKS,
            unit = "120 ml Cone",
            description = "Velvety vanilla bean soft-serve ice cream in a crunchy waffle cone with chocolate sauce.",
            calories = 190
        ),
        Product(
            id = 13,
            name = "Iced Latte",
            price = 9.00,
            emoji = "☕",
            category = ProductCategory.BEVERAGES,
            unit = "250 ml Can",
            description = "Ready-to-drink Arabica cold-brewed espresso blended with rich whole milk.",
            badge = "Staff Pick",
            calories = 135
        ),
        Product(
            id = 14,
            name = "Potato Chips",
            price = 3.50,
            emoji = "🥔",
            category = ProductCategory.SNACKS,
            unit = "85g Bag",
            description = "Artisanal kettle-cooked potato chips seasoned with fine sea salt.",
            calories = 160
        )
    )

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(setOf(1, 2, 5))
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun addToCart(product: Product, quantityDelta: Int = 1) {
        _cartItems.update { current ->
            val existing = current.find { it.product.id == product.id }
            if (existing != null) {
                val newQty = existing.quantity + quantityDelta
                if (newQty <= 0) {
                    current.filter { it.product.id != product.id }
                } else {
                    current.map {
                        if (it.product.id == product.id) it.copy(quantity = newQty) else it
                    }
                }
            } else if (quantityDelta > 0) {
                current + CartItem(product = product, quantity = quantityDelta)
            } else {
                current
            }
        }
    }

    fun setQuantity(product: Product, quantity: Int) {
        _cartItems.update { current ->
            if (quantity <= 0) {
                current.filter { it.product.id != product.id }
            } else {
                val existing = current.find { it.product.id == product.id }
                if (existing != null) {
                    current.map {
                        if (it.product.id == product.id) it.copy(quantity = quantity) else it
                    }
                } else {
                    current + CartItem(product = product, quantity = quantity)
                }
            }
        }
    }

    fun removeFromCart(productId: Int) {
        _cartItems.update { current ->
            current.filter { it.product.id != productId }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun toggleFavorite(productId: Int) {
        _favoriteIds.update { current ->
            if (current.contains(productId)) current - productId else current + productId
        }
    }

    fun addOrder(order: Order) {
        _orders.update { current ->
            listOf(order) + current
        }
    }
}
