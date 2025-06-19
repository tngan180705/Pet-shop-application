package com.example.dacs3.view

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dacs3.R
import com.example.dacs3.api.ApiClient
import com.example.dacs3.controller.HomeController
import com.example.dacs3.model.CategoryType
import com.example.dacs3.model.Product
import com.example.dacs3.model.SubCategory
import com.example.dacs3.ui.theme.DACS3Theme

@Composable
fun HomeView(
    onSearch: (String) -> Unit,
    onProductClick: (Int) -> Unit,
    onHomeClick: () -> Unit,
    onAccountClick: () -> Unit,
    onCartClick: () -> Unit,
    onNewsClick: () -> Unit
) {
    val homeController = remember { HomeController(ApiClient.userApi) }
    var categories by remember { mutableStateOf<Map<CategoryType, List<SubCategory>>>(emptyMap()) }
    var featured by remember { mutableStateOf<List<Product>>(emptyList()) }
    var bestSelling by remember { mutableStateOf<List<Product>>(emptyList()) }
    var subCategoryProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var allProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    var selectedSubCategory by remember { mutableStateOf<SubCategory?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Product>>(emptyList()) }

    // Cập nhật searchResults khi searchQuery hoặc allProducts thay đổi
    LaunchedEffect(searchQuery, allProducts) {
        Log.d("HomeView", "Search query: $searchQuery, All products: ${allProducts.size}")
        if (searchQuery.isBlank()) {
            searchResults = emptyList()
        } else {
            searchResults = allProducts.filter { product ->
                product.name.contains(searchQuery, ignoreCase = true) ||
                        product.description.contains(searchQuery, ignoreCase = true)
            }
            Log.d("HomeView", "Search results: ${searchResults.size} items")
        }
    }

    // Lấy danh mục, sản phẩm nổi bật, bán chạy và tất cả sản phẩm khi HomeView được khởi tạo
    LaunchedEffect(Unit) {
        homeController.getCategories { fetchedCategories ->
            Log.d("HomeView", "Fetched categories: $fetchedCategories")
            if (fetchedCategories.isEmpty()) {
                errorMessage = "Không thể tải danh mục. Vui lòng kiểm tra kết nối."
            } else {
                errorMessage = null
                categories = fetchedCategories
            }
        }
        homeController.getFeaturedProducts { products ->
            Log.d("HomeView", "Fetched featured products: ${products.size} items")
            featured = products
        }
        homeController.getBestSellingProducts { products ->
            Log.d("HomeView", "Fetched best selling products: ${products.size} items")
            bestSelling = products
        }
        homeController.getAllProducts { products ->
            Log.d("HomeView", "Fetched all products: ${products.size} items")
            allProducts = products
        }
    }

    // Lấy sản phẩm theo danh mục con khi selectedSubCategory thay đổi
    LaunchedEffect(selectedSubCategory) {
        selectedSubCategory?.let { subCategory ->
            Log.d("HomeView", "Fetching products for subcategory: ${subCategory.namesubctg}")
            homeController.getProductsBySubCategory(subCategory.idsubctg) { products ->
                subCategoryProducts = products
                Log.d("HomeView", "Fetched products for subcategory ${subCategory.namesubctg}: $products")
            }
        } ?: run {
            subCategoryProducts = emptyList()
        }
    }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            item {
                HeaderSection(
                    searchQuery = searchQuery,
                    onSearch = { query ->
                        searchQuery = query
                        onSearch(query)
                    }
                )
            }
            item {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    CategoryFilterSection(
                        categories = categories,
                        onSubCategoryClick = { subCategory ->
                            Log.d("HomeView", "Subcategory clicked: ${subCategory.namesubctg}")
                            selectedSubCategory = subCategory
                        }
                    )
                }
            }

            // Hiển thị kết quả tìm kiếm nếu có searchQuery
            if (searchQuery.isNotBlank()) {
                item {
                    if (searchResults.isNotEmpty()) {
                        ProductSection(
                            title = "Kết quả tìm kiếm",
                            products = searchResults,
                            onProductClick = onProductClick,
                            context = context
                        )
                    } else {
                        Text(
                            text = "Không tìm thấy sản phẩm phù hợp với '$searchQuery'.",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Hiển thị sản phẩm theo danh mục con nếu có
                if (selectedSubCategory != null && subCategoryProducts.isNotEmpty()) {
                    item {
                        ProductSection(
                            title = "Sản phẩm ${selectedSubCategory?.namesubctg}",
                            products = subCategoryProducts,
                            onProductClick = onProductClick,
                            context = context
                        )
                    }
                } else if (selectedSubCategory != null && subCategoryProducts.isEmpty()) {
                    item {
                        Text(
                            text = "Không có sản phẩm nào trong danh mục này.",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Hiển thị sản phẩm nổi bật và bán chạy
                item { ProductSection("Sản phẩm nổi bật", featured, onProductClick, context) }
                item { ProductSection("Sản phẩm bán chạy", bestSelling, onProductClick, context) }
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

@Composable
fun HeaderSection(
    searchQuery: String,
    onSearch: (String) -> Unit
) {
    var currentBannerIndex by remember { mutableStateOf(0) }

    val bannerImages = listOf(
        R.drawable.bn1,
        R.drawable.bn2,
        R.drawable.bn3,
        R.drawable.bn4
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "PetShop",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            TextField(
                value = searchQuery,
                onValueChange = onSearch,
                placeholder = { Text("Tìm kiếm sản phẩm...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Icon(
                Icons.Default.Person,
                contentDescription = "Login Icon",
                modifier = Modifier.size(32.dp)
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = bannerImages[currentBannerIndex]),
                contentDescription = "Banner",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = {
                    currentBannerIndex = if (currentBannerIndex > 0) currentBannerIndex - 1 else bannerImages.size - 1
                },
                modifier = Modifier.align(Alignment.CenterStart).padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Trước")
            }

            IconButton(
                onClick = {
                    currentBannerIndex = (currentBannerIndex + 1) % bannerImages.size
                },
                modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Tiếp theo")
            }
        }
    }
}
//hien thi danh sach theo hang ngang
@Composable
fun ProductSection(title: String, products: List<Product>, onProductClick: (Int) -> Unit, context: Context) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color(0xFF33CCFF),
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        LazyRow {
            items(products) { product ->
                ProductCard(product, onProductClick, context)
            }
        }
    }
}
// hien thi thong tin sp
@Composable
fun ProductCard(product: Product, onProductClick: (Int) -> Unit, context: Context) {
    val imageName = product.imageUrls?.firstOrNull()?.substringAfterLast("/") ?: "default"
    val fileName = imageName.removeSuffix(".jpg").removeSuffix(".png")
    val resourceId = context.resources.getIdentifier(fileName, "drawable", context.packageName)
    val imageResource = if (resourceId != 0) resourceId else R.drawable.d1

    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(8.dp)
            .clickable { onProductClick(product.id) },
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
// hien thi dm va dm con
@Composable
fun CategoryFilterSection(
    categories: Map<CategoryType, List<SubCategory>>,
    onSubCategoryClick: (SubCategory) -> Unit
) {
    var selectedCategory by remember(categories) {
        mutableStateOf(categories.keys.firstOrNull())
    }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        if (categories.isNotEmpty() && selectedCategory != null) {
            TabRow(
                selectedTabIndex = categories.keys.indexOf(selectedCategory) ?: 0,
                modifier = Modifier.padding(horizontal = 8.dp),
                containerColor = Color.White
            ) {
                categories.keys.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        text = {
                            Text(
                                category.namectg,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val subCategories = categories[selectedCategory] ?: emptyList()
            LazyRow(modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
                items(subCategories) { sub ->
                    CategoryChip(
                        categoryName = sub.namesubctg,
                        onClick = { onSubCategoryClick(sub) }
                    )
                }
            }
        } else {
            Text(
                text = "Đang tải danh mục...",
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CategoryChip(categoryName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
            .height(60.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun FooterSection(
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

        items.forEachIndexed { index, item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icons[index],
                    contentDescription = item,
                    tint = Color.White,
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
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}



