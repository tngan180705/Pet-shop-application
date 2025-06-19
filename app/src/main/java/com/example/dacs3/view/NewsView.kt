package com.example.dacs3.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
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
fun NewsView(
    onNewsClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val newsController = remember { NewsController() }
    var newsArticles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Lấy danh sách tin tức khi khởi tạo
    LaunchedEffect(Unit) {
        newsController.getAllNews { articles ->
            if (articles.isEmpty()) {
                errorMessage = "Không thể tải tin tức. Vui lòng kiểm tra kết nối."
            } else {
                errorMessage = null
                newsArticles = articles
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            item {
                // Tiêu đề "Tin tức thú cưng"
                Text(
                    text = "Tin tức thú cưng",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFF33CCFF),
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(newsArticles) { article ->
                NewsCard(article, onClick = { onNewsClick(article.id) })
            }
        }
        FooterSection(
            onHomeClick = onHomeClick,
            onNewsClick = { /* Đã ở trang tin tức, không làm gì */ },
            onAccountClick = onAccountClick,
            onCartClick = onCartClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun NewsCard(article: NewsArticle, onClick: () -> Unit) {
    // Gán drawable dựa trên ID bài tin tức
    val drawableMap = mapOf(
        1 to R.drawable.ald3,
        2 to R.drawable.golden3,
        3 to R.drawable.pk8,
        4 to R.drawable.beagle1
    )
    val imageResource = drawableMap[article.id] ?: R.drawable.d1

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = article.title,
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewsViewPreview() {
    DACS3Theme {
        NewsView(
            onNewsClick = {},
            onHomeClick = {},
            onAccountClick = {},
            onCartClick = {}
        )
    }
}