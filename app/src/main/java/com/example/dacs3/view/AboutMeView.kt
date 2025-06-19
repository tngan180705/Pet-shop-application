package com.example.dacs3.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import coil.compose.rememberImagePainter
import androidx.compose.ui.text.style.TextDecoration
import com.example.dacs3.controller.AboutMeController

@Composable
fun AboutMeView(
    aboutMeController: AboutMeController,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val aboutMe = remember { aboutMeController.getAboutMe() }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Các thông tin của chúng tôi",
                style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF33CCFF), fontSize = 24.sp),
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Hiển thị bản đồ (placeholder)
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(bottom = 16.dp).background(Color.LightGray)) {
                Text("Bản đồ của shop", textAlign = TextAlign.Center)
            }

            // Thông tin shop
            Text(text = "Địa chỉ: ${aboutMe.address}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Số điện thoại: ${aboutMe.phone}", style = MaterialTheme.typography.bodyMedium)
            ClickableText(
                text = AnnotatedString("Link Facebook"),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Blue, textDecoration = TextDecoration.LineThrough),
                onClick = { /* Mở liên kết đến Facebook */ }
            )
            Text(text = "Mục đích hoạt động: ${aboutMe.purpose}", style = MaterialTheme.typography.bodyMedium)

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