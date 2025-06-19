package com.example.dacs3.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
@Composable
fun LoginRegisterScreen(
    onLoginSuccess: (String, String) -> Unit,
    onRegisterSuccess: (String, String, String) -> Unit
) {
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4DA8E0), Color(0xFFB1E0F0)) // Gradient xanh dương nhạt
                )
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color.White // Màu nền trắng cho form
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isLogin) {
                        LoginForm(
                            onLoginSuccess = onLoginSuccess,
                            onSwitchToRegister = { isLogin = false },
                            onError = { errorMessage = it }
                        )
                    } else {
                        RegisterForm(
                            onRegisterSuccess = onRegisterSuccess,
                            onSwitchToLogin = { isLogin = true },
                            onError = { errorMessage = it }
                        )
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    onLoginSuccess: (String, String) -> Unit,
    onSwitchToRegister: () -> Unit,
    onError: (String) -> Unit
) {
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFB1E0F0) // Đổi sang màu xanh nhạt
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Đăng nhập", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = phone.text.isNotEmpty() && !phone.text.matches(Regex("\\d{10}"))
            )
            if (phone.text.isNotEmpty() && !phone.text.matches(Regex("\\d{10}"))) {
                Text(
                    text = "Số điện thoại phải có 10 chữ số",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật Khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (!isLoading) {
                        if (phone.text.isEmpty() || password.text.isEmpty()) {
                            onError("Vui lòng điền đầy đủ thông tin!")
                            return@Button
                        }
                        if (!phone.text.matches(Regex("\\d{10}"))) {
                            onError("Số điện thoại phải có 10 chữ số!")
                            return@Button
                        }
                        isLoading = true
                        onLoginSuccess(phone.text, password.text)
                        println("Login button clicked: phone=${phone.text}, password=${password.text}")
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4D93F4))
            ) {
                Text(if (isLoading) "Đang xử lý..." else "Đăng Nhập")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onSwitchToRegister() }) {
                Text("Chưa có tài khoản? Đăng ký")
            }
        }
    }
}

@Composable
fun RegisterForm(
    onRegisterSuccess: (String, String, String) -> Unit,
    onSwitchToLogin: () -> Unit,
    onError: (String) -> Unit
) {
    var phone by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFB1E0F0), // Đổi sang màu xanh nhạt
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Đăng ký", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên người dùng") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Số điện thoại") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật Khẩu") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (username.text.isEmpty() || phone.text.length != 10 || password.text.isEmpty()) {
                        errorMessage = "Vui lòng điền đầy đủ thông tin và đảm bảo số điện thoại có 10 số!"
                        onError(errorMessage)
                    } else {
                        errorMessage = ""
                        onRegisterSuccess(username.text, phone.text, password.text)
                        println("Register button clicked: $username, $phone, $password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4D93F4))
            ) {
                Text("Đăng Ký")
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { onSwitchToLogin() }) {
                Text("Đã có tài khoản? Đăng nhập")
            }
        }
    }
}