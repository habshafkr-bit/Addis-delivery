package com.example.model

enum class ProductCategory(val displayName: String, val icon: String) {
    ALL("All Items", "🛒"),
    DAIRY("Dairy & Eggs", "🥛"),
    BAKERY("Bakery", "🍞"),
    FRUITS("Fruits & Veg", "🍎"),
    BEVERAGES("Beverages", "🧃"),
    SNACKS("Snacks & Sweets", "🍫")
}

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val emoji: String,
    val category: ProductCategory,
    val unit: String = "1 pc",
    val description: String = "",
    val badge: String? = null,
    val calories: Int? = null
)

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double
        get() = product.price * quantity
}

enum class PaymentMethod(val label: String, val icon: String) {
    CASH("Cash on Delivery", "💵"),
    MADA("Mada / Apple Pay", "💳"),
    CARD("Credit Card", "💳")
}

data class Order(
    val id: String,
    val timestamp: Long,
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double,
    val deliveryFee: Double,
    val vat: Double,
    val total: Double,
    val deliveryAddress: String,
    val paymentMethod: PaymentMethod
)
