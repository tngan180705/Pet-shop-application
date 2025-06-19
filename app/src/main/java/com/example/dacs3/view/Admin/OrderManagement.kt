@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dacs3.view.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.dacs3.controller.AdminOrderController
import com.example.dacs3.model.Order
import com.example.dacs3.model.OrderDetail
import com.example.dacs3.ui.theme.NavyBlue
import java.text.SimpleDateFormat
import java.text.ParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderManagement() {
    val controller = remember { AdminOrderController() }
    val orders by controller.orders.collectAsState()
    val isLoading by controller.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Order?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Lọc trực tiếp
    val filteredOrders = orders.filter {
        it.id.toString().contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Quản lý đơn hàng",
            style = MaterialTheme.typography.titleLarge.copy(
                color = NavyBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm đơn hàng...") },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyBlue,
                    unfocusedBorderColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            successMessage?.let {
                Text(
                    text = it,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (filteredOrders.isEmpty()) {
                Text(
                    text = "Không tìm thấy đơn hàng",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                OrderTable(
                    orders = filteredOrders,
                    onOrderClick = { order ->
                        selectedOrder = order
                        showDetailDialog = true
                    },
                    onEditOrder = { order ->
                        println("Nút sửa đơn hàng được nhấn: idorder=${order.idorder}")
                        selectedOrder = order
                        showEditDialog = true
                    },
                    onDeleteOrder = { order ->
                        println("Nút xóa đơn hàng được nhấn: idorder=${order.idorder}")
                        showDeleteDialog = order
                    }
                )
            }
        }

        if (showEditDialog && selectedOrder != null) {
            OrderEditDialog(
                order = selectedOrder!!,
                onDismiss = {
                    showEditDialog = false
                    errorMessage = null
                },
                onSave = { updatedOrder ->
                    controller.updateOrder(
                        updatedOrder,
                        onSuccess = {
                            successMessage = "Cập nhật đơn hàng thành công!"
                            errorMessage = null
                            showEditDialog = false
                        },
                        onError = { error ->
                            errorMessage = error
                            successMessage = null
                        }
                    )
                }
            )
        }

        if (showDeleteDialog != null) {
            ConfirmDeleteDialog(
                order = showDeleteDialog!!,
                onConfirm = {
                    println("Xác nhận xóa đơn hàng: idorder=${showDeleteDialog!!.idorder}")
                    controller.deleteOrder(
                        showDeleteDialog!!.idorder,
                        onSuccess = {
                            successMessage = "Xóa đơn hàng thành công!"
                            errorMessage = null
                            showDeleteDialog = null
                        },
                        onError = { error ->
                            errorMessage = error
                            successMessage = null
                            showDeleteDialog = null
                        }
                    )
                },
                onDismiss = {
                    showDeleteDialog = null
                    errorMessage = null
                }
            )
        }

        if (showDetailDialog && selectedOrder != null) {
            OrderDetailDialog(
                order = selectedOrder!!,
                onDismiss = {
                    showDetailDialog = false
                    errorMessage = null
                }
            )
        }
    }
}

@Composable
fun OrderTable(
    orders: List<Order>,
    onOrderClick: (Order) -> Unit,
    onEditOrder: (Order) -> Unit,
    onDeleteOrder: (Order) -> Unit
) {
    fun formatDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate)
        } catch (e: ParseException) {
            date
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyBlue)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "STT",
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                "ID ĐH",
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                "ID KH",
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                "Ngày đặt",
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                "Trạng thái",
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                "Hành động",
                modifier = Modifier.width(120.dp),
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        Divider(color = Color.Gray)

        LazyColumn {
            itemsIndexed(orders, key = { _, order -> order.idorder }) { index, order ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${index + 1}",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        order.idorder.toString(),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOrderClick(order) },
                        color = Color.Blue,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        order.id.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        formatDate(order.orderdate),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        order.status,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.width(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                println("Edit button clicked: idorder=${order.idorder}")
                                onEditOrder(order)
                            }
                        ) {
                            Text("Sửa", color = NavyBlue)
                        }
                        TextButton(
                            onClick = {
                                println("Delete button clicked: idorder=${order.idorder}")
                                onDeleteOrder(order)
                            }
                        ) {
                            Text("Xóa", color = Color.Red)
                        }
                    }
                }
                Divider(color = Color.Gray.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(order: Order, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xác nhận xóa", style = MaterialTheme.typography.titleLarge) },
        text = { Text("Bạn có chắc muốn xóa đơn hàng ID '${order.idorder}'?") },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Xóa", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.textButtonColors(contentColor = NavyBlue)
            ) {
                Text("Hủy")
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun OrderDetailDialog(order: Order, onDismiss: () -> Unit) {
    fun formatDate(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat("dd-MM-yyyy")
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate)
        } catch (e: ParseException) {
            date
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chi tiết đơn hàng", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text("ID Đơn hàng: ${order.idorder}", style = MaterialTheme.typography.bodyMedium)
                Text("ID Người dùng: ${order.id}", style = MaterialTheme.typography.bodyMedium)
                Text("Ngày đặt: ${formatDate(order.orderdate)}", style = MaterialTheme.typography.bodyMedium)
                Text("Trạng thái: ${order.status}", style = MaterialTheme.typography.bodyMedium)
                Text("Tổng tiền: ${order.total}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Chi tiết sản phẩm:", style = MaterialTheme.typography.titleSmall)
                if (order.details.isEmpty()) {
                    Text("Không có chi tiết sản phẩm", style = MaterialTheme.typography.bodyMedium)
                } else {
                    order.details.forEach { detail ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Sản phẩm ID: ${detail.idsp}, Số lượng: ${detail.quantity}, Giá: ${detail.price}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = NavyBlue)
            }
        },
        containerColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderEditDialog(
    order: Order,
    onDismiss: () -> Unit,
    onSave: (Order) -> Unit
) {
    var customerId by remember { mutableStateOf(order.id.toString()) }
    var orderDate by remember { mutableStateOf(order.orderdate) }
    var status by remember { mutableStateOf(order.status) }
    var totalPrice by remember { mutableStateOf(order.total.toString()) }
    var errorMessage by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val statusOptions = listOf("Chờ xác nhận", "Đang giao hàng", "Đã hoàn thành")
    val dateRegex = Regex("""^\d{4}-\d{2}-\d{2}$""")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa đơn hàng", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                OutlinedTextField(
                    value = customerId,
                    onValueChange = { customerId = it },
                    label = { Text("ID Người dùng") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = customerId.isNotBlank() && customerId.toIntOrNull() == null
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = orderDate,
                    onValueChange = { orderDate = it },
                    label = { Text("Ngày đặt (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = orderDate.isNotBlank() && !orderDate.matches(dateRegex)
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        label = { Text("Trạng thái") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        },
                        isError = status.isBlank() && errorMessage.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NavyBlue,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = totalPrice,
                    onValueChange = { totalPrice = it },
                    label = { Text("Tổng tiền") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = totalPrice.isNotBlank() && totalPrice.toDoubleOrNull() == null
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        customerId.isBlank() ->
                            errorMessage = "ID người dùng không được để trống"
                        customerId.toIntOrNull() == null ->
                            errorMessage = "ID người dùng phải là số hợp lệ"
                        orderDate.isBlank() ->
                            errorMessage = "Ngày đặt không được để trống"
                        !orderDate.matches(dateRegex) ->
                            errorMessage = "Ngày đặt phải có định dạng YYYY-MM-DD"
                        status.isBlank() ->
                            errorMessage = "Trạng thái không được để trống"
                        totalPrice.isBlank() ->
                            errorMessage = "Tổng tiền không được để trống"
                        totalPrice.toDoubleOrNull() == null ->
                            errorMessage = "Tổng tiền phải là số hợp lệ"
                        else -> {
                            val updatedOrder = Order(
                                idorder = order.idorder,
                                id = customerId.toInt(),
                                status = status,
                                orderdate = orderDate,
                                total = totalPrice.toDouble(),
                                details = order.details
                            )
                            onSave(updatedOrder)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Lưu", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = NavyBlue)
            }
        },
        containerColor = Color.White
    )
}