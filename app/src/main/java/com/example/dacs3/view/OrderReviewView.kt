package com.example.dacs3.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.dacs3.model.OrderReview

@Composable
fun OrderReviewView(
    reviews: List<OrderReview>,
    onProductClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Các đánh giá của bạn",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF33CCFF),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            LazyColumn {
                items(reviews) { review ->
                    OrderReviewCard(review) { productId ->
                        onProductClick(productId)
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
@Preview(showBackground = true)
@Composable
fun PreviewOrderReviewView() {
    val sampleReviews = listOf(
        OrderReview(1, "Sản phẩm A", 5, "2025-04-01", "Sản phẩm rất tốt!", "https://example.com/image_a.jpg"),
        OrderReview(2, "Sản phẩm B", 4, "2025-03-15", "Hài lòng với chất lượng.", "https://example.com/image_b.jpg")
    )

    OrderReviewView(
        reviews = sampleReviews,
        onProductClick = {},
        onHomeClick = {},
        onNewsClick = {},
        onAccountClick = {},
        onCartClick = {}
    )
}