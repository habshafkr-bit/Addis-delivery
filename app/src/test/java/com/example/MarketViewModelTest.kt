package com.example

import com.example.data.MarketRepository
import com.example.model.PaymentMethod
import com.example.model.ProductCategory
import com.example.viewmodel.MarketTab
import com.example.viewmodel.MarketViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MarketViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MarketRepository
    private lateinit var viewModel: MarketViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = MarketRepository()
        viewModel = MarketViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCatalogHasCoreProducts() {
        val products = repository.catalogProducts
        assertTrue(products.isNotEmpty())

        val productNames = products.map { it.name }
        assertTrue(productNames.contains("Milk"))
        assertTrue(productNames.contains("Bread"))
        assertTrue(productNames.contains("Apple"))
        assertTrue(productNames.contains("Banana"))
        assertTrue(productNames.contains("Water"))
        assertTrue(productNames.contains("Juice"))
        assertTrue(productNames.contains("Eggs"))
        assertTrue(productNames.contains("Chocolate"))
    }

    @Test
    fun testAddToCartAndTotalCalculation() {
        val milk = repository.catalogProducts.first { it.name == "Milk" }
        assertEquals(6.0, milk.price, 0.001)

        viewModel.addToCart(milk)
        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals(1, viewModel.cartItems.value[0].quantity)
        assertEquals(6.0, viewModel.cartItems.value[0].totalPrice, 0.001)

        // Add again
        viewModel.addToCart(milk)
        assertEquals(1, viewModel.cartItems.value.size)
        assertEquals(2, viewModel.cartItems.value[0].quantity)
        assertEquals(12.0, viewModel.cartItems.value[0].totalPrice, 0.001)

        // Decrement
        viewModel.decrementFromCart(milk)
        assertEquals(1, viewModel.cartItems.value[0].quantity)

        // Decrement to zero removes item
        viewModel.decrementFromCart(milk)
        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun testRemoveFromCart() {
        val bread = repository.catalogProducts.first { it.name == "Bread" }
        viewModel.addToCart(bread)
        assertEquals(1, viewModel.cartItems.value.size)

        viewModel.removeFromCart(bread.id)
        assertEquals(0, viewModel.cartItems.value.size)
    }

    @Test
    fun testPromoCodeApplication() {
        viewModel.onPromoCodeChanged("MINI10")
        viewModel.applyPromoCode()

        assertEquals(0.10, viewModel.uiState.value.discountPercent, 0.001)
        assertNotNull(viewModel.uiState.value.promoCodeSuccess)
    }

    @Test
    fun testConfirmOrderClearsCartAndAddsToHistory() {
        val water = repository.catalogProducts.first { it.name == "Water" }
        viewModel.addToCart(water)
        viewModel.onPaymentMethodChanged(PaymentMethod.CASH)

        viewModel.confirmOrder()

        assertEquals(0, viewModel.cartItems.value.size)
        assertEquals(1, viewModel.orderHistory.value.size)
        val order = viewModel.orderHistory.value[0]
        assertEquals(PaymentMethod.CASH, order.paymentMethod)
        assertTrue(viewModel.uiState.value.showOrderSuccessDialog)
    }

    @Test
    fun testTabSelection() {
        viewModel.onTabSelected(MarketTab.CART)
        assertEquals(MarketTab.CART, viewModel.uiState.value.currentTab)

        viewModel.onTabSelected(MarketTab.ORDERS)
        assertEquals(MarketTab.ORDERS, viewModel.uiState.value.currentTab)
    }
}
