
package com.example.dacs3.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dacs3.R
import com.example.dacs3.api.ApiClient
import com.example.dacs3.controller.FavouriteProductController
import com.example.dacs3.model.Product
import com.example.dacs3.ui.theme.DACS3Theme

@Composable
fun FavouriteProductView(
    favouriteProductController: FavouriteProductController = ApiClient.favouriteProductController,
    userId: Int,
    onProductClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    var favouriteProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != 0) {
            favouriteProductController.getFavouriteProducts(userId) { products ->
                favouriteProducts = products
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val context = LocalContext.current // Lấy Context để truy cập tài nguyên drawable

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Danh sách các sản phẩm yêu thích",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color(0xFF33CCFF),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (favouriteProducts.isEmpty()) {
                Text(
                    text = "Chưa có sản phẩm yêu thích nào!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(favouriteProducts) { product ->
                        FavouriteProductCard(product, context) { productId ->
                            onProductClick(productId)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            FooterSection(
                currentScreen = "favourite",
                onHomeClick = onHomeClick,
                onNewsClick = onNewsClick,
                onAccountClick = onAccountClick,
                onCartClick = onCartClick
            )
        }
    }
}

@Composable
fun FavouriteProductCard(product: Product, context: Context, onProductClick: (Int) -> Unit) {
    val imageName = product.imageUrls?.firstOrNull()?.substringAfterLast("/")?.removeSuffix(".jpg")?.removeSuffix(".png") ?: "default"
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    val imageResource = if (resourceId != 0) resourceId else R.drawable.d1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onProductClick(product.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = "Favourite Product Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "${product.price} VNĐ", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewFavouriteProductView() {
    DACS3Theme {
        FavouriteProductView(
            userId = 1,
            onProductClick = {},
            onHomeClick = {},
            onNewsClick = {},
            onAccountClick = {},
            onCartClick = {}
        )
    }
}