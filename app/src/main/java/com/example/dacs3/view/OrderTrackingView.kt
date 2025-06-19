package com.example.dacs3.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.dacs3.controller.OrderController
import com.example.dacs3.model.Order

@Composable
fun OrderTrackingView(
    phone: String,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val orderController = OrderController()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(phone) {
        if (phone.isNotEmpty()) {
            orderController.getOrders(phone) { fetchedOrders ->
                orders = fetchedOrders
                isLoading = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Theo dõi đơn hàng",
                style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFADD8E6)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (orders.isEmpty()) {
                Text(
                    text = "Không có đơn hàng nào để hiển thị.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(orders) { order ->
                        OrderCard(order)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            FooterSection(
                onHomeClick = onHomeClick,
                onNewsClick = onNewsClick,
                onAccountClick = onAccountClick,
                onCartClick = onCartClick
            )
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Mã đơn hàng: ${order.idorder}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Thời gian: ${order.orderdate}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Tổng: ${order.total} VNĐ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Trạng thái: ${order.status}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Hiển thị danh sách sản phẩm (nếu có)
            order.details?.forEach { detail ->
                Row(
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = rememberImagePainter("https://via.placeholder.com/40"),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Sản phẩm ID: ${detail.idsp}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Số lượng: ${detail.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Giá: ${detail.price} VNĐ",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FooterSection(
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFADD8E6))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val items = listOf("Trang chủ", "Tin tức", "Giỏ hàng", "Tài khoản")
        val icons = listOf(
            Icons.Default.Home,
            Icons.Default.Newspaper,
            Icons.Default.ShoppingCart,
            Icons.Default.Person
        )
        val actions = listOf(onHomeClick, onNewsClick, onCartClick, onAccountClick)

        items.forEachIndexed { index, item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icons[index],
                    contentDescription = item,
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { actions[index]() }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickable { actions[index]() }
                )
            }
        }
    }
}