package com.example.dacs3.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dacs3.R
import com.example.dacs3.controller.NewsController
import com.example.dacs3.model.NewsArticle
import com.example.dacs3.ui.theme.DACS3Theme

@Composable
fun NewsDetailView(
    newsId: Int,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val newsController = remember { NewsController() }
    var article by remember { mutableStateOf<NewsArticle?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Lấy chi tiết bài tin tức
    LaunchedEffect(newsId) {
        newsController.getNewsDetail(newsId) { fetchedArticle ->
            if (fetchedArticle == null) {
                errorMessage = "Không thể tải bài tin tức. Vui lòng thử lại."
            } else {
                errorMessage = null
                article = fetchedArticle
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            item {
                // Nút quay lại
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else if (article != null) {
                    // Hiển thị chi tiết bài tin tức
                    // Gán drawable dựa trên ID bài tin tức
                    val drawableMap = mapOf(
                        1 to R.drawable.ald3,
                        2 to R.drawable.golden3,
                        3 to R.drawable.pk8,
                        4 to R.drawable.beagle1
                    )
                    val imageResource = drawableMap[article!!.id] ?: R.drawable.d1

                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = article!!.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = article!!.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ngày đăng: ${article!!.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = article!!.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = article!!.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        FooterSection(
            onHomeClick = onHomeClick,
            onNewsClick = onNewsClick,
            onAccountClick = onAccountClick,
            onCartClick = onCartClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NewsDetailViewPreview() {
    DACS3Theme {
        NewsDetailView(
            newsId = 1,
            onBackClick = {},
            onHomeClick = {},
            onNewsClick = {},
            onAccountClick = {},
            onCartClick = {}
        )
    }
}