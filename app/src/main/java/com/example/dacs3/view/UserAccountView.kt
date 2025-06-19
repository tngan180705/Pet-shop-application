package com.example.dacs3.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dacs3.controller.UserController
import com.example.dacs3.model.User

val LightBlue = Color(0xFF33CCFF)

@Composable
fun UserAcountView(
    userController: UserController,
    phone: String,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onCartClick: () -> Unit,
    onOrderTrackingClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onReviewClick: () -> Unit,
    onInforShopClick: () -> Unit,
    onLogoutClick: () -> Unit // Thêm tham số onLogoutClick
) {
    var user by remember { mutableStateOf<User?>(null) }
    val isEditing = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var updateMessage by remember { mutableStateOf<String?>(null) }

    // Lấy thông tin người dùng từ API khi màn hình được tạo
    LaunchedEffect(phone) {
        userController.fetchUserFromApi(phone) { fetchedUser ->
            user = fetchedUser
            if (fetchedUser != null) {
                println("Fetched user: ${fetchedUser.username} with phone: $phone")
            } else {
                println("No user found for phone: $phone")
            }
        }
    }

    // Hiển thị thông báo và làm mới dữ liệu sau khi cập nhật
    LaunchedEffect(updateMessage) {
        updateMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            if (message == "Cập nhật thông tin thành công!") {
                userController.fetchUserFromApi(phone) { fetchedUser ->
                    user = fetchedUser
                    println("User updated: ${fetchedUser?.username}")
                }
            }
            updateMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (user == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    if (isEditing.value) {
                        EditAccountForm(user) { updatedUser ->
                            userController.updateUser(phone, updatedUser) { message ->
                                isEditing.value = false
                                updateMessage = message
                            }
                        }
                    } else {
                        HeaderSection1(user, onEditClick = {
                            isEditing.value = true
                        })
                        Spacer(modifier = Modifier.height(24.dp))
                        BodySection(
                            onOrderTrackingClick = onOrderTrackingClick,
                            onFavouriteClick = onFavouriteClick,
                            onReviewClick = onReviewClick,
                            onInforShopClick = onInforShopClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onLogoutClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Đăng xuất", color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        FooterSection(
                            onHomeClick = onHomeClick,
                            onNewsClick = onNewsClick,
                            onCartClick = onCartClick,
                            onAccountClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderSection1(user: User?, onEditClick: () -> Unit) {
    if (user != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .padding(16.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LightBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = user.email ?: "Chưa có email",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Số điện thoại: ${user.phone}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Địa chỉ: ${user.address ?: "Chưa có địa chỉ"}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
                ) {
                    Text("Sửa thông tin", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BodySection(
    onOrderTrackingClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    onReviewClick: () -> Unit,
    onInforShopClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Theo dõi đơn hàng",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White)
                .padding(16.dp)
                .clickable { onOrderTrackingClick() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sản phẩm yêu thích",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White)
                .padding(16.dp)
                .clickable { onFavouriteClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Thông tin của shop",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White)
                .padding(16.dp)
                .clickable(onClick = onInforShopClick)
        )
    }
}

@Composable
fun EditAccountForm(user: User?, onConfirm: (User) -> Unit) {
    if (user == null) {
        Text("Không tìm thấy thông tin người dùng", color = Color.Red)
        return
    }

    val username = remember { mutableStateOf(user.username) }
    val email = remember { mutableStateOf(user.email ?: "") }
    val phone = remember { mutableStateOf(user.phone) }
    val address = remember { mutableStateOf(user.address ?: "") }
    val profilePicture = remember { mutableStateOf(user.profilePicture ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .wrapContentHeight()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Chỉnh sửa thông tin",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = LightBlue,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone.value,
            onValueChange = { phone.value = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = address.value,
            onValueChange = { address.value = it },
            label = { Text("Địa chỉ") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = profilePicture.value,
            onValueChange = { profilePicture.value = it },
            label = { Text("Link ảnh đại diện") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val updatedUser = user.copy(
                    id = user.id,
                    username = username.value,
                    email = if (email.value.isNotEmpty()) email.value else null,
                    phone = phone.value,
                    address = address.value.trim().ifEmpty { "" },
                    profilePicture = profilePicture.value.trim().ifEmpty { "" },
                    password = user.password
                )
                onConfirm(updatedUser)
            },
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Xác nhận", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserAcountView() {
    val userController = UserController()
    UserAcountView(
        userController = userController,
        phone = "0566699305",
        onHomeClick = {},
        onNewsClick = {},
        onCartClick = {},
        onOrderTrackingClick = {},
        onFavouriteClick = {},
        onReviewClick = {},
        onInforShopClick = {},
        onLogoutClick = {}
    )
}