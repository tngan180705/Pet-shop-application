package com.example.dacs3.api

import com.example.dacs3.controller.CommentController
import com.example.dacs3.controller.DetailProductController
import com.example.dacs3.controller.FavouriteProductController
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2/php_api/"
    private var retrofit: Retrofit? = null
    val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    fun init(context: android.content.Context) {
        if (retrofit == null) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }

    val userApi: UserApi
        get() = retrofit?.create(UserApi::class.java) ?: throw IllegalStateException("ApiClient not initialized")

    val detailProductController: DetailProductController
        get() = DetailProductController(userApi)

    val favouriteProductController: FavouriteProductController
        get() = FavouriteProductController(userApi)
    val commentController: CommentController
        get() = CommentController(userApi)
}