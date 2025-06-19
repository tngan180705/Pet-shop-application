
package com.example.dacs3.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs3.R
import com.example.dacs3.api.ApiClient
import com.example.dacs3.controller.DetailProductController
import com.example.dacs3.controller.FavouriteProductController
import com.example.dacs3.controller.CartController
import com.example.dacs3.controller.CommentController
import com.example.dacs3.model.Product
import com.example.dacs3.model.CartItem
import com.example.dacs3.model.Comment
import kotlinx.coroutines.launch

@Composable
fun DetailProductView(
    productId: Int,
    cartController: CartController,
    detailProductController: DetailProductController = ApiClient.detailProductController,
    favouriteProductController: FavouriteProductController = ApiClient.favouriteProductController,
    userId: Int,
    onFavorite: () -> Unit,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit,
    onProductClick: (Int) -> Unit // Thêm callback để xử lý click vào sản phẩm liên quan
) {
    // Log instance của cartController
    println("DetailProductView: Using CartController instance: $cartController")

    val product = remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var relatedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var favoriteActionResult by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(productId) {
        println("Fetching product detail for productId: $productId")
        detailProductController.getProductDetail(productId) { fetchedProduct ->
            println("Product fetched: $fetchedProduct")
            product.value = fetchedProduct
        }
        detailProductController.getRelatedProducts(productId) { products ->
            println("Related products fetched: $products")
            relatedProducts = products
        }
    }

    LaunchedEffect(favoriteActionResult) {
        favoriteActionResult?.let { message ->
            snackbarHostState.showSnackbar(message)
            favoriteActionResult = null
        }
    }

    val context = LocalContext.current // Lấy Context để truy cập tài nguyên drawable

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            ) {
                item { HeaderSection(onHomeClick, onNewsClick, onAccountClick, onCartClick) }
                if (product.value == null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                            Text("Đang tải thông tin sản phẩm...", modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                } else {
                    // Trong DetailProductView, trong LazyColumn
                    product.value?.let { prod ->
                        item { ProductTitle(prod.name) }
                        item { ProductImageCarousel(prod.imageUrls, context) }
                        item { ProductPriceAndQuantity(prod.price, quantity, onQuantityChange = { quantity = it }) }
                        item {
                            println("Rendering ActionButtons, isLoading: $isLoading")
                            ActionButtons(
                                onAddToCart = {
                                    println("Button 'Thêm vào giỏ hàng' clicked for product: $prod")
                                    val cartItem = CartItem(
                                        id = prod.id,
                                        name = prod.name,
                                        price = prod.price,
                                        imageUrl = prod.imageUrls?.firstOrNull() ?: "",
                                        quantity = quantity
                                    )
                                    println("Creating cart item: $cartItem")
                                    cartController.addItem(cartItem)
                                    println("Cart items after adding: ${cartController.cartItems}")
                                    coroutineScope.launch {
                                        println("Showing snackbar")
                                        snackbarHostState.showSnackbar("Đã thêm vào giỏ hàng!")
                                    }
                                    println("Navigating to CartView")
                                    onCartClick()
                                },
                                onFavorite = {
                                    println("Button 'Yêu thích' clicked")
                                    if (userId != 0 && productId != 0) {
                                        isLoading = true
                                        favouriteProductController.addFavouriteProduct(userId, productId) { success ->
                                            isLoading = false
                                            println("Favorite action completed, success: $success, isLoading: $isLoading")
                                            favoriteActionResult = if (success) {
                                                "Đã thêm vào danh sách yêu thích"
                                            } else {
                                                "Không thể thêm vào danh sách yêu thích"
                                            }
                                        }
                                    } else {
                                        favoriteActionResult = "Vui lòng đăng nhập để thêm yêu thích"
                                    }
                                },
                                isLoading = isLoading
                            )
                        }
                        item { ProductDescription(prod.description) }
                        item { CommentSection(productId, userId, snackbarHostState = snackbarHostState) } // Thêm CommentSection
                        item { RelatedProductsSection(relatedProducts, context, onProductClick) }
                    }
                }
            }
            FooterSection(
                currentScreen = "detailProduct",
                onHomeClick = onHomeClick,
                onNewsClick = onNewsClick,
                onAccountClick = onAccountClick,
                onCartClick = onCartClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun HeaderSection(
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFADD8E6)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "PetShop",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                color = Color.White
            )
        )
        Row {
            Icon(
                Icons.Default.Home,
                contentDescription = "Trang chủ",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onHomeClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Newspaper,
                contentDescription = "Tin tức",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNewsClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Giỏ hàng",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onCartClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Person,
                contentDescription = "Tài khoản",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onAccountClick() }
            )
        }
    }
}

@Composable
fun ProductTitle(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.titleLarge.copy(
            color = Color(0xFF33CCFF),
            fontSize = 24.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun ProductImageCarousel(imageUrls: List<String>?, context: Context) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(200.dp)
    ) {
        items(imageUrls ?: listOf("default")) { url ->
            val imageName = url.substringAfterLast("/").removeSuffix(".jpg").removeSuffix(".png")
            val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            val imageResource = if (resourceId != 0) resourceId else R.drawable.d1

            Image(
                painter = painterResource(id = imageResource),
                contentDescription = "Hình ảnh sản phẩm",
                modifier = Modifier
                    .size(200.dp)
                    .padding(end = 8.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ProductPriceAndQuantity(price: Double, quantity: Int, onQuantityChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Giá: ${price} VNĐ",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF33CCFF))
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Số lượng:",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { if (quantity > 1) onQuantityChange(quantity - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Text("-", style = MaterialTheme.typography.bodyLarge)
            }
            Text(
                text = "$quantity",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = { onQuantityChange(quantity + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Text("+", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun ActionButtons(onAddToCart: () -> Unit, onFavorite: () -> Unit, isLoading: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                println("Add to cart button pressed")
                onAddToCart()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33CCFF)),
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thêm vào giỏ hàng", color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {
                println("Favorite button pressed")
                onFavorite()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33CCFF)),
            modifier = Modifier.weight(1f),
            enabled = !isLoading
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yêu thích", color = Color.White)
        }
    }
    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ProductDescription(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Mô tả sản phẩm",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF33CCFF))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description.takeIf { it.isNotBlank() } ?: "Không có mô tả",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun RelatedProductsSection(relatedProducts: List<Product>, context: Context, onProductClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Sản phẩm liên quan",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF33CCFF))
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (relatedProducts.isEmpty()) {
            Text(
                text = "Không có sản phẩm liên quan",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        } else {
            LazyRow {
                items(relatedProducts) { product ->
                    ProductCard(product, context, onProductClick)
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, context: Context, onProductClick: (Int) -> Unit) {
    val imageName = product.imageUrls?.firstOrNull()?.substringAfterLast("/")?.removeSuffix(".jpg")?.removeSuffix(".png") ?: "default"
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
    val imageResource = if (resourceId != 0) resourceId else R.drawable.d1

    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
            .clickable { onProductClick(product.id) }, // Thêm sự kiện click
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = imageResource),
                contentDescription = product.name,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${product.price} VNĐ",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF33CCFF)
            )
        }
    }
}
@Composable
fun CommentSection(
    productId: Int,
    userId: Int,
    commentController: CommentController = ApiClient.commentController,
    snackbarHostState: SnackbarHostState
) {
    var rating by remember { mutableStateOf(0) }
    var commentText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Lấy danh sách bình luận khi productId thay đổi
    LaunchedEffect(productId) {
        commentController.getComments(productId) { fetchedComments ->
            println("Fetched comments: $fetchedComments")
            comments = fetchedComments
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Đánh giá sản phẩm",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF33CCFF))
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Hàng hiển thị sao
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (1..5).forEach { star ->
                Icon(
                    imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Star $star",
                    tint = if (star <= rating) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { rating = star }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hàng nhập bình luận và nút gửi
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Nhập bình luận của bạn...") },
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                enabled = !isSubmitting
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (userId == 0) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Vui lòng đăng nhập để gửi bình luận")
                        }
                        return@Button
                    }
                    if (productId <= 0) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("ID sản phẩm không hợp lệ")
                        }
                        return@Button
                    }
                    if (rating == 0) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Vui lòng chọn số sao")
                        }
                        return@Button
                    }
                    if (commentText.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Vui lòng nhập bình luận")
                        }
                        return@Button
                    }

                    isSubmitting = true
                    val comment = Comment(
                        idsp = productId,
                        id = userId,
                        rate = rating,
                        description = commentText
                    )
                    println("Sending comment: $comment")
                    commentController.addComment(comment) { success, errorMessage ->
                        isSubmitting = false
                        coroutineScope.launch {
                            if (success) {
                                snackbarHostState.showSnackbar("Bình luận đã được gửi")
                                rating = 0
                                commentText = ""
                                // Làm mới danh sách bình luận
                                commentController.getComments(productId) { fetchedComments ->
                                    comments = fetchedComments
                                }
                            } else {
                                snackbarHostState.showSnackbar(errorMessage ?: "Không thể gửi bình luận")
                            }
                        }
                    }
                },
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF33CCFF))
            ) {
                Text("Gửi", color = Color.White)
            }
        }

        if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Danh sách bình luận
        Text(
            text = "Bình luận (${comments.size})",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF33CCFF))
        )
        if (comments.isEmpty()) {
            Text(
                text = "Chưa có bình luận nào.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = comment.username ?: "Người dùng #${comment.id}", // Hiển thị username
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            (1..5).forEach { star ->
                Icon(
                    imageVector = if (star <= comment.rate) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (star <= comment.rate) Color(0xFFFFC107) else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = comment.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
@Composable
fun FooterSection(
    currentScreen: String,
    onHomeClick: () -> Unit,
    onNewsClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFADD8E6))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val items = listOf("Trang chủ", "Tin tức", "Giỏ hàng", "Tài khoản")
        val icons = listOf(
            Icons.Default.Home,
            Icons.Default.Newspaper,
            Icons.Default.ShoppingCart,
            Icons.Default.Person
        )
        val screenNames = listOf("home", "news", "cart", "account")

        items.forEachIndexed { index, item ->
            val isSelected = currentScreen == screenNames[index]
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icons[index],
                    contentDescription = item,
                    tint = if (isSelected) Color.Yellow else Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            when (index) {
                                0 -> onHomeClick()
                                1 -> onNewsClick()
                                2 -> onCartClick()
                                3 -> onAccountClick()
                            }
                        }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item,
                    color = if (isSelected) Color.Yellow else Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable {
                        when (index) {
                            0 -> onHomeClick()
                            1 -> onNewsClick()
                            2 -> onCartClick()
                            3 -> onAccountClick()
                        }
                    }
                )
            }
        }
    }
}