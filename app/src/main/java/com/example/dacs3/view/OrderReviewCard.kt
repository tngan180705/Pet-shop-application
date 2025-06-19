package com.example.dacs3.view

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import com.example.dacs3.model.OrderReview

@Composable
fun OrderReviewCard(review: OrderReview, onProductClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onProductClick(review.productId) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(review.productImage),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(end = 16.dp)
            )
            Column {
                Text(text = review.productName, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Rating: ${review.rating} ★", style = MaterialTheme.typography.bodyMedium)
                Text(text = review.reviewDate, style = MaterialTheme.typography.bodySmall)
                Text(text = review.reviewDescription, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewOrderReviewCard() {
    val sampleReview = OrderReview(
        productId = 1,
        productName = "Sản phẩm A",
        rating = 5,
        reviewDate = "2025-04-01",
        reviewDescription = "Sản phẩm rất tốt!",
        productImage = "https://example.com/image_a.jpg"
    )

    OrderReviewCard(review = sampleReview, onProductClick = {})
}