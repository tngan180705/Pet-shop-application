// File: com.example.dacs3/MainActivity.kt

package com.example.dacs3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.dacs3.api.ApiClient
import com.example.dacs3.controller.*
import com.example.dacs3.model.CategoryType
import com.example.dacs3.model.NewsArticle
import com.example.dacs3.model.OrderReview
import com.example.dacs3.model.Order
import com.example.dacs3.model.Product
import com.example.dacs3.model.SubCategory
import com.example.dacs3.model.User
import com.example.dacs3.ui.theme.DACS3Theme
import com.example.dacs3.view.*
import com.example.dacs3.view.Admin.AdminView
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            ApiClient.init(applicationContext)
            println("ApiClient initialized successfully")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Lỗi khởi tạo API: ${e.message}", Toast.LENGTH_LONG).show()
            return
        }

        setContent {
            DACS3Theme {
                val homeController = HomeController(ApiClient.userApi)
                val context = LocalContext.current
                val userController = UserController()
                val orderController = OrderController()
                val favouriteProductController = ApiClient.favouriteProductController
                val aboutMeController = AboutMeController()
                val detailProductController = ApiClient.detailProductController
                val orderReviewController = OrderReviewController()

                var currentScreen by remember { mutableStateOf("login") }
                var isLoggedIn by remember { mutableStateOf(false) }
                var userId by remember { mutableStateOf(0) }
                var username by remember { mutableStateOf("") }
                var currentPhone by remember { mutableStateOf("") }
                var userAddress by remember { mutableStateOf("") }
                var selectedProductId by remember { mutableStateOf(0) }
                var reviews by remember { mutableStateOf(emptyList<OrderReview>()) }
                var favouriteProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
                var orders by remember { mutableStateOf<List<Order>>(emptyList()) }

                val cartController = remember(currentPhone) {
                    CartController(context, currentPhone)
                }

                val sampleArticles = listOf(
                    NewsArticle(1, "Tiêu đề bài báo 1", "Tóm tắt bài báo 1", "21/04/2025"),
                    NewsArticle(2, "Tiêu đề bài báo 2", "Tóm tắt bài báo 2", "21/04/2025"),
                    NewsArticle(3, "Tiêu đề bài báo 3", "Tóm tắt bài báo 3", "21/04/2025")
                )

                val scope = rememberCoroutineScope()

                LaunchedEffect(currentPhone) {
                    if (currentPhone.isNotEmpty()) {
                        userController.getUserInfo(currentPhone) { user, message ->
                            if (user != null) {
                                username = user.username
                                userAddress = user.address ?: "Chưa có địa chỉ"
                                userId = user.id
                                println("Loaded user info: username=$username, address=$userAddress, userId=$userId")
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                username = ""
                                userAddress = "Chưa có địa chỉ"
                                userId = 0
                            }
                        }
                        orderController.getOrders(currentPhone) { orderList ->
                            orders = orderList
                        }
                    }
                }

                LaunchedEffect(userId) {
                    try {
                        if (userId != 0) {
                            favouriteProductController.getFavouriteProducts(userId) { products ->
                                favouriteProducts = products
                            }
                        }
                    } catch (e: Exception) {
                        println("Error loading favourite products: ${e.message}")
                    }
                }
                var selectedNewsId by remember { mutableStateOf<Int?>(null) }
                val navigateToHome = { currentScreen = "home"; println("Navigating to HomeView") }
                val navigateToNews = { currentScreen = "news"; println("Navigating to NewsView") }
                val navigateToCart = { currentScreen = "cart"; println("Navigating to CartView") }
                val navigateToAccount = { currentScreen = "account"; println("Navigating to UserAccountView") }
                val navigateToFavourite = { currentScreen = "favouriteProducts"; println("Navigating to FavouriteProductView") }
                val navigateToOrderConfirm = { currentScreen = "orderConfirm"; println("Navigating to OrderConfirmView") }
                val navigateToDetailProduct = { productId: Int ->
                    selectedProductId = productId
                    currentScreen = "detailProduct"
                    println("Navigating to DetailProductView for productId: $productId")
                }
                val navigateToLogin = {
                    currentScreen = "login"
                    isLoggedIn = false
                    userId = 0
                    username = ""
                    currentPhone = ""
                    userAddress = ""
                    favouriteProducts = emptyList()
                    orders = emptyList()
                    println("Logged out, navigating to Login screen")
                }

                when (currentScreen) {
                    "login" -> LoginRegisterScreen(
                        onLoginSuccess = { phone, password ->
                            scope.launch {
                                userController.loginUser(phone, password) { message, user ->
                                    if (user != null) {
                                        isLoggedIn = true
                                        userId = user.id
                                        username = user.username
                                        currentPhone = phone
                                        if (user.isAdmin == 1) {
                                            currentScreen = "admin"
                                            println("Logged in as admin with phone: $phone, userId: $userId")
                                        } else {
                                            currentScreen = "home"
                                            println("Logged in as user with username: $username, phone: $phone, userId: $userId")
                                        }
                                    }
                                }
                            }
                        },
                        onRegisterSuccess = { username, phone, password ->
                            scope.launch {
                                val newUser = User(
                                    id = 0,
                                    username = username,
                                    phone = phone,
                                    password = password,
                                    address = "Chưa có địa chỉ"
                                )
                                userController.registerUser(newUser) { message ->
                                    Toast.makeText(context, "Register: $message", Toast.LENGTH_SHORT).show()
                                    if (message.contains("thành công", ignoreCase = true)) {
                                        currentScreen = "login"
                                    }
                                }
                            }
                        }
                    )
                    "home" -> HomeView(
                        onSearch = { query -> println("Tìm kiếm sản phẩm: $query") },
                        onProductClick = navigateToDetailProduct,
                        onHomeClick = navigateToHome,
                        onAccountClick = navigateToAccount,
                        onCartClick = navigateToCart,
                        onNewsClick = navigateToNews
                    )
                    "news" -> NewsView(
                        onNewsClick = { id ->
                            selectedNewsId = id
                            currentScreen = "news_detail"
                        },
                        onHomeClick = { currentScreen = "home" },
                        onAccountClick = { currentScreen = "account" },
                        onCartClick = { currentScreen = "cart" }
                    )
                    "news_detail" -> NewsDetailView(
                        newsId = selectedNewsId ?: 1,
                        onBackClick = { currentScreen = "news" },
                        onHomeClick = { currentScreen = "home" },
                        onNewsClick = { currentScreen = "news" },
                        onAccountClick = { currentScreen = "account" },
                        onCartClick = { currentScreen = "cart" }
                    )
                    "cart" -> {
                        println("Navigating to CartView with CartController: $cartController")
                        CartView(
                            cartController = cartController,
                            onHomeClick = navigateToHome,
                            onNewsClick = navigateToNews,
                            onAccountClick = navigateToAccount,
                            onCartClick = navigateToCart,
                            onCheckoutClick = navigateToOrderConfirm
                        )
                    }
                    "orderConfirm" -> {
                        println("Navigating to OrderConfirmView with CartController: $cartController")
                        OrderConfirmView(
                            phone = currentPhone,
                            cartItems = cartController.cartItems,
                            totalPrice = cartController.totalPrice,
                            onConfirmOrder = {
                                orderController.createOrder(
                                    phone = currentPhone,
                                    cartItems = cartController.cartItems,
                                    total = cartController.totalPrice
                                ) { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    if (message.contains("thành công", ignoreCase = true)) {
                                        cartController.clearCart()
                                        navigateToHome()
                                        orderController.getOrders(currentPhone) { orderList ->
                                            orders = orderList
                                        }
                                    }
                                }
                            },
                            onHomeClick = navigateToHome,
                            onNewsClick = navigateToNews,
                            onAccountClick = navigateToAccount,
                            onCartClick = navigateToCart
                        )
                    }
                    "account" -> UserAcountView(
                        userController = userController,
                        phone = currentPhone,
                        onHomeClick = navigateToHome,
                        onNewsClick = navigateToNews,
                        onCartClick = navigateToCart,
                        onOrderTrackingClick = { currentScreen = "orderTracking" },
                        onFavouriteClick = navigateToFavourite,
                        onReviewClick = {
                            reviews = orderReviewController.getOrderReviews(currentPhone)
                            currentScreen = "orderReview"
                        },
                        onInforShopClick = { currentScreen = "aboutMe" },
                        onLogoutClick = navigateToLogin
                    )
                    "detailProduct" -> {
                        println("Navigating to DetailProductView with CartController: $cartController")
                        DetailProductView(
                            productId = selectedProductId,
                            cartController = cartController,
                            detailProductController = detailProductController,
                            favouriteProductController = favouriteProductController,
                            userId = userId,
                            onFavorite = navigateToFavourite,
                            onHomeClick = navigateToHome,
                            onNewsClick = navigateToNews,
                            onAccountClick = navigateToAccount,
                            onCartClick = navigateToCart,
                            onProductClick = navigateToDetailProduct // Truyền callback để xử lý click sản phẩm liên quan
                        )
                    }
                    "aboutMe" -> AboutMeView(
                        aboutMeController = aboutMeController,
                        onHomeClick = navigateToHome,
                        onNewsClick = navigateToNews,
                        onAccountClick = navigateToAccount,
                        onCartClick = navigateToCart
                    )
                    "orderTracking" -> OrderTrackingView(
                        phone = currentPhone,
                        onHomeClick = navigateToHome,
                        onNewsClick = navigateToNews,
                        onAccountClick = navigateToAccount,
                        onCartClick = navigateToCart
                    )
                    "orderReview" -> OrderReviewView(
                        reviews = reviews,
                        onProductClick = navigateToDetailProduct,
                        onHomeClick = navigateToHome,
                        onNewsClick = navigateToNews,
                        onAccountClick = navigateToAccount,
                        onCartClick = navigateToCart
                    )
                    "favouriteProducts" -> FavouriteProductView(
                        favouriteProductController = favouriteProductController,
                        userId = userId,
                        onProductClick = navigateToDetailProduct,
                        onHomeClick = navigateToHome,
                        onNewsClick = navigateToNews,
                        onAccountClick = navigateToAccount,
                        onCartClick = navigateToCart
                    )
                    "admin" -> AdminView()
                }
            }
        }
    }
}

// Dữ liệu mẫu cho Preview
private val previewCategories = mapOf(
    CategoryType(1, "Chó", listOf(
        SubCategory(1, "Samoyed"),
        SubCategory(2, "Alaska"),
        SubCategory(3, "Phốc sóc")
    )) to listOf(
        SubCategory(1, "Samoyed"),
        SubCategory(2, "Alaska"),
        SubCategory(3, "Phốc sóc")
    ),
    CategoryType(2, "Mèo", listOf(
        SubCategory(4, "Mèo Anh lông ngắn"),
        SubCategory(5, "Mèo Anh lông dài")
    )) to listOf(
        SubCategory(4, "Mèo Anh lông ngắn"),
        SubCategory(5, "Mèo Anh lông dài")
    )
)

private val previewFeaturedProducts = listOf(
    Product(1, "Sản phẩm Nổi bật 1", 100.0, "Mô tả sản phẩm 1", listOf("http://10.0.2.2/php_api/images/beagle1.jpg")),
    Product(2, "Sản phẩm Nổi bật 2", 150.0, "Mô tả sản phẩm 2", listOf("http://10.0.2.2/php_api/images/bn1.jpg"))
)

private val previewNewsArticles = listOf(
    NewsArticle(1, "Tiêu đề bài báo 1", "Tóm tắt bài báo 1", "21/04/2025"),
    NewsArticle(2, "Tiêu đề bài báo 2", "Tóm tắt bài báo 2", "21/04/2025")
)