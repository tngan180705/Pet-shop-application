package com.example.dacs3.view.Admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs3.controller.AdminController
import com.example.dacs3.model.User
import com.example.dacs3.ui.theme.NavyBlue
import com.example.dacs3.ui.theme.LightGray

@Composable
fun AccountManagement(
    controller: AdminController,
    selectedUser: User? = null,
    onUserClick: (User) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var viewingUser by remember { mutableStateOf<User?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        controller.loadUsersFromApi(
            onUsersLoaded = { userList ->
                println("Users loaded in controller: $userList")
                isLoading = false
            },
            onError = { error ->
                errorMessage = error
                isLoading = false
                println("Error in AccountManagement: $error")
            }
        )
    }

    val filteredUsers by remember(searchQuery) {
        derivedStateOf {
            controller.searchUsers(searchQuery)
        }
    }
    println("Filtered users: $filteredUsers")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(LightGray)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Quản lý tài khoản",
            style = MaterialTheme.typography.headlineMedium,
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
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
            ) {
                Text("Thêm tài khoản", color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm kiếm") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { /* Tìm kiếm được xử lý tự động qua searchQuery */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Tìm kiếm",
                    tint = NavyBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        UserTable(
            users = filteredUsers,
            isLoading = isLoading,
            onUserClick = { user ->
                viewingUser = user
                onUserClick(user)
            },
            onDeleteUser = { userId ->
                controller.deleteUserFromApi(
                    userId = userId,
                    onSuccess = {
                        Toast.makeText(context, "Xóa tài khoản thành công!", Toast.LENGTH_SHORT).show()
                    },
                    onError = { error ->
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )

        viewingUser?.let {
            UserDetailDialog(user = it, onDismiss = { viewingUser = null })
        }

        if (showAddDialog) {
            AddUserDialog(
                onDismiss = { showAddDialog = false },
                onSave = { newUser ->
                    controller.addUserToApi(
                        user = newUser,
                        onSuccess = {
                            Toast.makeText(context, "Thêm tài khoản thành công!", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun UserTable(
    users: List<User>,
    isLoading: Boolean,
    onUserClick: (User) -> Unit,
    onDeleteUser: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ID",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Tên người dùng",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(2f),

            )
            Text(
                text = "Số điện thoại",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(2f)
            )
            Text(
                text = "Hành động",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1.5f)
            )
        }

        Divider()

        if (!isLoading && users.isEmpty()) {
            Text(
                text = "Không tìm thấy người dùng",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn {
                items(users) { user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onUserClick(user) },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.id.toString(),
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = user.username,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(2f),
                            color = Color.Blue
                        )
                        Text(
                            text = user.phone,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(2f)
                        )
                        Button(
                            onClick = { onDeleteUser(user.id) },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Xóa", fontSize = 12.sp)
                        }
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun UserDetailDialog(user: User, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chi tiết người dùng") },
        text = {
            Column {
                Text("ID: ${user.id}")
                Text("Tên tài khoản: ${user.username}")
                Text("Mật khẩu: ${user.password}")
                Text("Số điện thoại: ${user.phone}")
                Text("Email: ${user.email ?: "N/A"}")
                Text("Địa chỉ: ${user.address ?: "N/A"}")
                Text("Ảnh đại diện: ${user.profilePicture ?: "N/A"}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun AddUserDialog(onDismiss: () -> Unit, onSave: (User) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm người dùng mới") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên tài khoản") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Địa chỉ") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAdmin,
                        onCheckedChange = { isAdmin = it }
                    )
                    Text("Là Admin")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                        onSave(
                            User(
                                id = 0,
                                username = "",
                                phone = "",
                                password = "",
                                email = null,
                                address = null,
                                profilePicture = null,
                                isAdmin = 0
                            )
                        ) // Gửi user rỗng để báo lỗi
                    } else {
                        val newUser = User(
                            id = 0,
                            username = username,
                            phone = phone,
                            password = password,
                            email = if (email.isNotEmpty()) email else null,
                            address = if (address.isNotEmpty()) address else null,
                            profilePicture = null,
                            isAdmin = if (isAdmin) 1 else 0
                        )
                        onSave(newUser)
                    }
                }
            ) {
                Text("Thêm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountManagement() {
    val controller = AdminController()
    val sampleUser = User(
        id = 1,
        username = "tngan",
        phone = "0566699305",
        password = "123",
        email = "tngan@example.com",
        address = "119/33 Phạm Như Xương"
    )
    AccountManagement(
        controller = controller,
        selectedUser = sampleUser,
        onUserClick = { }
    )
}