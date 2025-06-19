

package com.example.dacs3.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dacs3.R
import com.example.dacs3.controller.CartController
import com.example.dacs3.model.CartItem

@Composable
fun CartView(
    cartController: CartController,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    println("CartView: Using CartController instance: $cartController")

    LaunchedEffect(Unit) {
        println("CartView: Initial cart items on navigation: ${cartController.cartItems}")
    }

    LaunchedEffect(cartController.cartItems) {
        println("CartView: Cart items updated: ${cartController.cartItems}")
    }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection1()
            if (cartController.cartItems.isEmpty()) {
                Text(
                    "Giỏ hàng của bạn đang trống.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartController.cartItems) { item ->
                        CartItemView(cartController, item, context)
                    }
                }
                TotalPriceSection(
                    totalPrice = cartController.totalPrice,
                    onCheckoutClick = onCheckoutClick
                )
            }

            FooterSection(
                currentScreen = "cart",
                onHomeClick = onHomeClick,
                onNewsClick = onNewsClick,
                onAccountClick = onAccountClick,
                onCartClick = onCartClick
            )
        }
    }
}

@Composable
fun HeaderSection1() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFADD8E6)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Giỏ hàng", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CartItemView(cartController: CartController, item: CartItem, context: Context) {
    val imageName = item.imageUrl.substringAfterLast("/").removeSuffix(".jpg").removeSuffix(".png")
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    val imageResource = if (resourceId != 0) resourceId else R.drawable.d1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = item.name,
            modifier = Modifier
                .size(100.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "${item.price} VNĐ", style = MaterialTheme.typography.bodyMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Số lượng: ", style = MaterialTheme.typography.bodyMedium)
                var quantity by remember { mutableStateOf(item.quantity) }
                Button(onClick = {
                    if (quantity > 1) {
                        quantity--
                        cartController.updateQuantity(item, quantity)
                    }
                }) {
                    Text("-")
                }
                Text(quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                Button(onClick = {
                    quantity++
                    cartController.updateQuantity(item, quantity)
                }) {
                    Text("+")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        cartController.removeItem(item)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("X", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun TotalPriceSection(totalPrice: Double, onCheckoutClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Tổng giá: ${totalPrice} VNĐ", color = Color.White)
        Button(onClick = onCheckoutClick) {
            Text("Thanh toán")
        }
    }
}