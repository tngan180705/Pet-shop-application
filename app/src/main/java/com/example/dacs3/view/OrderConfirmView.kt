// File: com.example.dacs3.view/OrderConfirmView.kt

package com.example.dacs3.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dacs3.R
import com.example.dacs3.controller.UserController
import com.example.dacs3.model.CartItem

@Composable
fun OrderConfirmView(
    phone: String,
    cartItems: List<CartItem>,
    totalPrice: Double,
    onConfirmOrder: () -> Unit,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val userController = UserController()

    var userName by remember { mutableStateOf("") }
    var userPhone by remember { mutableStateOf(phone) }
    var userAddress by remember { mutableStateOf("Chưa có địa chỉ") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(phone) {
        if (phone.isNotEmpty()) {
            userController.getUserInfo(phone) { user, message ->
                if (user != null) {
                    userName = user.username
                    userPhone = user.phone
                    userAddress = user.address ?: "Chưa có địa chỉ"
                    errorMessage = null
                    println("OrderConfirmView: Loaded user info - username=$userName, address=$userAddress")
                } else {
                    errorMessage = message
                    println("OrderConfirmView: Failed to load user info - $message")
                }
                isLoading = false
            }
        } else {
            errorMessage = "Không có số điện thoại để lấy thông tin"
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection()

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else if (errorMessage != null) {
                Text(
                    text = "Lỗi: $errorMessage",
                    color = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 112.dp)
                ) {
                    item {
                        OrderUserInfoSection(
                            userName = userName,
                            phone = userPhone,
                            address = userAddress
                        )
                    }

                    items(cartItems) { item ->
                        OrderItemView(item = item, context = context)
                    }
                }
            }

            TotalPriceAndConfirmSection(
                totalPrice = totalPrice,
                onConfirmOrder = onConfirmOrder
            )

            FooterSection(
                currentScreen = "orderConfirm",
                onHomeClick = onHomeClick,
                onNewsClick = onNewsClick,
                onAccountClick = onAccountClick,
                onCartClick = onCartClick
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFADD8E6)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Xác nhận đơn hàng",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}

@Composable
fun OrderUserInfoSection(userName: String, phone: String, address: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Thông tin người đặt",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF33CCFF))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tên: $userName",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Số điện thoại: $phone",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Địa chỉ: $address",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun OrderItemView(item: CartItem, context: Context) {
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
            Text(
                text = "Số lượng: ${item.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun TotalPriceAndConfirmSection(totalPrice: Double, onConfirmOrder: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Tổng tiền: ${totalPrice} VNĐ",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
        Button(
            onClick = onConfirmOrder,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33CCFF)),
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Text("Đặt hàng", color = Color.White)
        }
    }
}