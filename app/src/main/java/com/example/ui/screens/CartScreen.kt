package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CartItem
import com.example.model.PaymentMethod
import com.example.model.Product
import com.example.ui.theme.MarketGreenDark
import com.example.ui.theme.MarketGreenPrimary
import com.example.ui.theme.MarketRed

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    promoCode: String,
    promoSuccess: String?,
    promoError: String?,
    discountPercent: Double,
    deliveryAddress: String,
    paymentMethod: PaymentMethod,
    onAddToCart: (Product) -> Unit,
    onDecrement: (Product) -> Unit,
    onRemoveItem: (Int) -> Unit,
    onClearCart: () -> Unit,
    onPromoCodeChange: (String) -> Unit,
    onApplyPromo: () -> Unit,
    onAddressChange: (String) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onCheckoutClick: () -> Unit,
    onExploreShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = cartItems.sumOf { it.totalPrice }
    val discount = subtotal * discountPercent
    val deliveryFee = if (subtotal >= 50.0 || subtotal == 0.0) 0.0 else 5.0
    val vat = (subtotal - discount) * 0.15
    val total = (subtotal - discount) + deliveryFee + vat

    if (cartItems.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F7EE)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛍️", fontSize = 54.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.testTag("empty_cart_text")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Looks like you haven't added anything to your cart yet. Explore our fresh products!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onExploreShop,
                colors = ButtonDefaults.buttonColors(containerColor = MarketGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("explore_products_button")
            ) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Browse Mini Market")
            }
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .testTag("cart_items_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cart Header with Clear All
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🛍️ Shopping Cart",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.testTag("cart_heading")
                        )
                        Text(
                            text = "${cartItems.sumOf { it.quantity }} items",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    TextButton(
                        onClick = onClearCart,
                        colors = ButtonDefaults.textButtonColors(contentColor = MarketRed),
                        modifier = Modifier.testTag("clear_cart_button")
                    ) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }
            }

            // Cart Items
            items(
                items = cartItems,
                key = { it.product.id }
            ) { item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cart_item_${item.product.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Box
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF3F6F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.product.emoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Product Name & unit price
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = String.format("%.2f SAR each", item.product.price),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = String.format("Total: %.2f SAR", item.totalPrice),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MarketGreenPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.testTag("cart_item_total_${item.product.id}")
                            )
                        }

                        // Stepper + Delete Button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F7EE)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onDecrement(item.product) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Remove,
                                        contentDescription = "Decrease",
                                        tint = MarketGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Text(
                                    text = "${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MarketGreenDark
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )

                                IconButton(
                                    onClick = { onAddToCart(item.product) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Increase",
                                        tint = MarketGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            IconButton(
                                onClick = { onRemoveItem(item.product.id) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("remove_item_${item.product.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove product",
                                    tint = MarketRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Promo Code Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🏷️ Promo Voucher",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoCode,
                                onValueChange = onPromoCodeChange,
                                placeholder = { Text("e.g. MINI10") },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("promo_input"),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MarketGreenPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = onApplyPromo,
                                colors = ButtonDefaults.buttonColors(containerColor = MarketGreenPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("apply_promo_button")
                            ) {
                                Text("Apply")
                            }
                        }

                        if (promoSuccess != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = MarketGreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = promoSuccess, color = MarketGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        if (promoError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = MarketRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = promoError, color = MarketRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Delivery Details & Address Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MarketGreenPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Delivery Address",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliveryAddress,
                            onValueChange = onAddressChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("delivery_address_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MarketGreenPrimary
                            )
                        )
                    }
                }
            }

            // Payment Method Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, tint = MarketGreenPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Payment Method",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        PaymentMethod.values().forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPaymentMethodChange(method) }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == method,
                                    onClick = { onPaymentMethodChange(method) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MarketGreenPrimary)
                                )
                                Text(text = method.icon, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = method.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (paymentMethod == method) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Bill Summary & Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Order Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow("Subtotal", String.format("%.2f SAR", subtotal))

                        if (discount > 0.0) {
                            SummaryRow(
                                "Discount (${(discountPercent * 100).toInt()}%)",
                                String.format("-%.2f SAR", discount),
                                valueColor = MarketGreenPrimary
                            )
                        }

                        SummaryRow(
                            "Delivery Fee",
                            if (deliveryFee == 0.0) "FREE" else String.format("%.2f SAR", deliveryFee),
                            valueColor = if (deliveryFee == 0.0) MarketGreenPrimary else MaterialTheme.colorScheme.onSurface
                        )

                        SummaryRow("VAT (15%)", String.format("%.2f SAR", vat))

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = String.format("%.2f SAR", total),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MarketGreenPrimary
                                ),
                                modifier = Modifier.testTag("cart_grand_total")
                            )
                        }
                    }
                }
            }

            // Big Checkout Button
            item {
                Button(
                    onClick = onCheckoutClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MarketGreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("checkout_button")
                ) {
                    Text(
                        text = String.format("Checkout • %.2f SAR", total),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        )
    }
}
