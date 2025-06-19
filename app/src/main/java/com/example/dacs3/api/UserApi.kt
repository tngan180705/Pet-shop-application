package com.example.dacs3.api

import com.example.dacs3.model.CategoryType
import com.example.dacs3.model.OrderRequest
import com.example.dacs3.model.User
import com.example.dacs3.model.Product
import com.example.dacs3.model.Order
import com.example.dacs3.model.OrderDetail
import com.example.dacs3.model.NewsArticle
import com.example.dacs3.model.Comment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @POST("users.php?action=register")
    fun registerUser(@Body user: User): Call<ResponseMessage>

    @POST("users.php?action=login")
    fun loginUser(@Body credentials: UserCredentials): Call<ResponseMessage>

    @POST("users.php?action=update")
    fun updateUser(@Body user: User): Call<ResponseMessage>

    @GET("users.php?action=getUserByPhone")
    fun getUserByPhone(@Query("phone") phone: String): Call<ResponseMessage>

    @GET("categories.php")
    fun getCategories(): Call<List<CategoryType>>

    @GET("products.php")
    fun getProductsBySubCategory(@Query("subcategory_id") id: Int): Call<List<Product>>

    @GET("products.php?type=featured")
    fun getFeaturedProducts(): Call<List<Product>>

    @GET("products.php?type=best_selling")
    fun getBestSellingProducts(): Call<List<Product>>

    @GET("product_detail.php")
    fun getProductDetail(@Query("idsp") idsp: Int): Call<Product>

    @GET("product_detail.php?related=true")
    fun getRelatedProducts(@Query("idsp") idsp: Int): Call<List<Product>>

    @GET("favourites.php")
    fun addFavouriteProduct(
        @Query("action") action: String = "add",
        @Query("id") userId: Int,
        @Query("idsp") productId: Int
    ): Call<ResponseMessage>

    @GET("favourites.php")
    fun getFavouriteProducts(
        @Query("action") action: String = "get",
        @Query("id") userId: Int
    ): Call<ResponseMessageWrapper<List<Product>>>

    @GET("favourites.php")
    fun removeFavouriteProduct(
        @Query("action") action: String = "remove",
        @Query("id") userId: Int,
        @Query("idsp") productId: Int
    ): Call<ResponseMessage>

    @POST("orders.php?action=createOrder")
    fun createOrder(@Body orderRequest: OrderRequest): Call<ResponseMessage>

    @GET("orders.php?action=getOrders")
    fun getOrders(@Query("phone") phone: String): Call<ResponseMessageWrapper<List<Order>>>

    @POST("orders.php?action=updateOrderStatus")
    fun updateOrderStatus(@Body data: Map<String, Any>): Call<ResponseMessage>

    @GET("users.php?action=getAllUsers")
    fun getAllUsers(): Call<ResponseMessageWrapper<List<User>>>

    @GET("products.php?action=search")
    fun searchProducts(@Query("query") query: String): Call<List<Product>>

    @POST("users.php?action=delete")
    fun deleteUser(@Body deleteRequest: DeleteUserRequest): Call<ResponseMessageWrapper<Void>>

    @GET("products.php")
    fun getAllProducts(): Call<List<Product>>

    @POST("products.php?action=add")
    fun addProduct(@Body product: Product): Call<ResponseMessageWrapper<Void>>

    @POST("products.php?action=update")
    fun updateProduct(@Body product: Product): Call<ResponseMessageWrapper<Void>>

    @POST("products.php?action=delete")
    fun deleteProduct(@Body deleteRequest: DeleteProductRequest): Call<ResponseMessageWrapper<Void>>

    @POST("orders.php?action=deleteOrder")
    fun deleteOrder(@Body request: DeleteOrderRequest): Call<ResponseMessage>


    @GET("orders.php?action=getAllOrders")
    fun getAllOrders(): Call<ResponseMessageWrapper<List<Order>>>

    @POST("orders.php?action=updateOrder")
    fun updateOrder(@Body request: UpdateOrderRequest): Call<ResponseMessage>

    @GET("news.php?action=getAll")
    fun getAllNews(): Call<ResponseMessageWrapper<List<NewsArticle>>>

    @GET("news.php?action=getDetail")
    fun getNewsDetail(@Query("id") id: Int): Call<ResponseMessageWrapper<NewsArticle>>

    @POST("comments.php?action=add")
    fun addComment(@Body comment: Comment): Call<ResponseMessage>

    @GET("comments.php?action=get")
    fun getComments(@Query("idsp") productId: Int): Call<ResponseMessageWrapper<List<Comment>>>
}

data class UserCredentials(val phone: String, val password: String)
data class ResponseMessage(val message: String, val user: User? = null)
data class ResponseMessageWrapper<T>(val message: String, val data: T? = null)
data class DeleteUserRequest(val userId: Int)
data class DeleteProductRequest(val id: Int)
data class DeleteOrderRequest(val idorder: Int)

data class UpdateOrderRequest(
    val idorder: Int,
    val user_id: Int,
    val orderdate: String,
    val status: String,
    val total: Double
)

data class AddOrderRequest(
    val user_id: Int,
    val orderdate: String,
    val status: String,
    val total: Double,
    val details: List<OrderDetail>
)