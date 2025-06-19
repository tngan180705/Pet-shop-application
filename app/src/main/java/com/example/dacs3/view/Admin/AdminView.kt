@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.dacs3.view.Admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs3.R
import com.example.dacs3.controller.AdminController

import com.example.dacs3.controller.AdminProductController
import com.example.dacs3.model.User
import com.example.dacs3.model.Admin
import com.example.dacs3.view.admin.OrderManagement
import com.example.dacs3.view.admin.ProductManagement // Thêm dòng này
import com.example.dacs3.ui.theme.NavyBlue
import com.example.dacs3.ui.theme.LightGray
import kotlinx.coroutines.launch


@Composable
fun AdminView() {
    val controller = remember { AdminController() }
    val menuItems = listOf(
        Admin("Quản lý tài khoản"),
        Admin("Quản lý sản phẩm"),
        Admin("Quản lý đơn hàng")
    )
    val adminProductController = remember { AdminProductController() }
    var selectedItem by remember { mutableStateOf(menuItems[0]) }
    var currentTab by remember { mutableStateOf(0) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedUser by remember { mutableStateOf<User?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .background(NavyBlue),
                drawerContainerColor = NavyBlue
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.d1),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Admin", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(24.dp))

                    menuItems.forEachIndexed { index, item ->
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .clickable {
                                    selectedItem = item
                                    currentTab = index
                                    scope.launch { drawerState.close() }
                                }
                        )
                    }
                }
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(LightGray)) {
            TopAppBar(
                title = { Text("Trang Admin", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
            )

            when (currentTab) {
                0 -> AccountManagement(controller, selectedUser = selectedUser, onUserClick = { selectedUser = it })
                1 -> ProductManagement(adminProductController)
                2 -> OrderManagement()
            }
            selectedUser?.let {
                UserDetailDialog(user = it, onDismiss = { selectedUser = null })
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewAdminView() {
    AdminView()
}