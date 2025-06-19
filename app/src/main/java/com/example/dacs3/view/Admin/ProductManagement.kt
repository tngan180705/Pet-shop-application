
package com.example.dacs3.view.admin

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dacs3.R
import com.example.dacs3.controller.AdminProductController
import com.example.dacs3.model.Product
import com.example.dacs3.ui.theme.DACS3Theme
import com.example.dacs3.ui.theme.NavyBlue
import com.example.dacs3.ui.theme.LightGray
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagement(controller: AdminProductController) {
    val productList by controller.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val isLoading by controller.isLoading.collectAsState()
    val filteredList = productList.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val context = LocalContext.current // Lấy Context để truy cập tài nguyên drawable

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Quản lý sản phẩm",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
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
            Button(
                onClick = {
                    selectedProduct = null
                    showAddDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Thêm sản phẩm", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm sản phẩm...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
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

            if (filteredList.isEmpty()) {
                Text(
                    text = "Không tìm thấy sản phẩm",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(NavyBlue)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("STT", Modifier.width(40.dp), color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("ID", Modifier.width(50.dp), color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("Tên sản phẩm", Modifier.weight(1f), color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("Hành động", Modifier.width(150.dp), color = Color.White, style = MaterialTheme.typography.bodySmall)
                }

                Divider(color = Color.Gray)

                LazyColumn {
                    itemsIndexed(filteredList, key = { _, product -> product.id }) { index, product ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "${index + 1}",
                                Modifier.width(40.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "${product.id}",
                                Modifier.width(50.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                product.name,
                                Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedProduct = product
                                        showDetailDialog = true
                                    },
                                color = Color.Blue,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(modifier = Modifier.width(150.dp)) {
                                TextButton(
                                    onClick = {
                                        selectedProduct = product
                                        showEditDialog = true
                                    }
                                ) {
                                    Text("Sửa", color = NavyBlue)
                                }
                                TextButton(
                                    onClick = {
                                        controller.deleteProduct(
                                            product.id,
                                            onSuccess = {
                                                successMessage = "Xóa sản phẩm thành công!"
                                                errorMessage = null
                                            },
                                            onError = { error ->
                                                errorMessage = error
                                                successMessage = null
                                            }
                                        )
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

        if (showAddDialog) {
            ProductAddDialog(
                onDismiss = { showAddDialog = false },
                onSave = { product ->
                    controller.addProduct(
                        product,
                        onSuccess = {
                            successMessage = "Thêm sản phẩm thành công!"
                            errorMessage = null
                            showAddDialog = false
                        },
                        onError = { error ->
                            errorMessage = error
                            successMessage = null
                        }
                    )
                }
            )
        }

        if (showEditDialog && selectedProduct != null) {
            ProductEditDialog(
                product = selectedProduct!!,
                onDismiss = { showEditDialog = false },
                onSave = { product ->
                    controller.updateProduct(
                        product,
                        onSuccess = {
                            successMessage = "Cập nhật sản phẩm thành công!"
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

        if (showDetailDialog && selectedProduct != null) {
            ProductDetailDialog(
                product = selectedProduct!!,
                context = context, // Truyền context vào dialog
                onDismiss = { showDetailDialog = false }
            )
        }
    }
}

@Composable
fun ProductDetailDialog(product: Product, context: Context, onDismiss: () -> Unit) {
    val imageName = product.imageUrls?.firstOrNull()?.substringAfterLast("/")?.removeSuffix(".jpg")?.removeSuffix(".png") ?: "default"
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    val imageResource = if (resourceId != 0) resourceId else R.drawable.d1

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng", color = NavyBlue)
            }
        },
        title = {
            Text(
                product.name,
                style = MaterialTheme.typography.titleLarge.copy(color = NavyBlue)
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = imageResource),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("ID: ${product.id}", style = MaterialTheme.typography.bodyMedium)
                Text("Giá: ${product.price} VNĐ", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Mô tả:", style = MaterialTheme.typography.titleSmall)
                Text(
                    product.description.takeIf { it.isNotBlank() } ?: "Không có mô tả",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun ProductAddDialog(
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm sản phẩm", style = MaterialTheme.typography.titleLarge) },
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm") },
                    isError = name.isBlank() && errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL ảnh") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = (price.isNotBlank() && price.toDoubleOrNull() == null) && errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Tên sản phẩm không được để trống"
                        price.isNotBlank() && price.toDoubleOrNull() == null -> errorMessage = "Giá phải là số hợp lệ"
                        else -> {
                            val productToSave = Product(
                                id = 0,
                                name = name,
                                price = price.toDoubleOrNull() ?: 0.0,
                                description = description,
                                imageUrls = if (imageUrl.isNotBlank()) listOf(imageUrl) else null
                            )
                            onSave(productToSave)
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

@Composable
fun ProductEditDialog(
    product: Product,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var name by remember { mutableStateOf(product.name) }
    var imageUrl by remember { mutableStateOf(product.imageUrls?.firstOrNull() ?: "") }
    var price by remember { mutableStateOf(product.price.toString()) }
    var description by remember { mutableStateOf(product.description) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sửa sản phẩm", style = MaterialTheme.typography.titleLarge) },
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm") },
                    isError = name.isBlank() && errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL ảnh") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    isError = (price.isNotBlank() && price.toDoubleOrNull() == null) && errorMessage.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        name.isBlank() -> errorMessage = "Tên sản phẩm không được để trống"
                        price.isNotBlank() && price.toDoubleOrNull() == null -> errorMessage = "Giá phải là số hợp lệ"
                        else -> {
                            val productToSave = Product(
                                id = product.id,
                                name = name,
                                price = price.toDoubleOrNull() ?: 0.0,
                                description = description,
                                imageUrls = if (imageUrl.isNotBlank()) listOf(imageUrl) else null
                            )
                            onSave(productToSave)
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