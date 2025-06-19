package com.example.dacs3.controller

import com.example.dacs3.api.ResponseMessage
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.api.UserApi
import com.example.dacs3.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouriteProductController(private val userApi: UserApi) {
    fun addFavouriteProduct(userId: Int, productId: Int, callback: (Boolean) -> Unit) {
        println("Calling addFavouriteProduct with userId=$userId, productId=$productId")
        userApi.addFavouriteProduct("add", userId, productId).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                println("API Response: ${response.code()} - ${response.body()}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.message == "success") {
                        println("Add favourite successful: ${body.message}")
                        callback(true)
                    } else {
                        println("Add favourite failed: Invalid response message - ${body?.message}")
                        callback(false)
                    }
                } else {
                    println("Add favourite failed: HTTP ${response.code()} - ${response.errorBody()?.string()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                println("API Failure: ${t.message}")
                callback(false)
            }
        })
    }

    fun getFavouriteProducts(userId: Int, callback: (List<Product>) -> Unit) {
        println("Calling getFavouriteProducts with userId=$userId")
        userApi.getFavouriteProducts("get", userId).enqueue(object : Callback<ResponseMessageWrapper<List<Product>>> {
            override fun onResponse(call: Call<ResponseMessageWrapper<List<Product>>>, response: Response<ResponseMessageWrapper<List<Product>>>) {
                println("API Response: ${response.code()} - ${response.body()}")
                if (response.isSuccessful && response.body()?.message == "success") {
                    callback(response.body()?.data ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseMessageWrapper<List<Product>>>, t: Throwable) {
                println("API Failure: ${t.message}")
                callback(emptyList())
            }
        })
    }

    fun removeFavouriteProduct(userId: Int, productId: Int, callback: (Boolean) -> Unit) {
        println("Calling removeFavouriteProduct with userId=$userId, productId=$productId")
        userApi.removeFavouriteProduct("remove", userId, productId).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                println("API Response: ${response.code()} - ${response.body()}")
                callback(response.isSuccessful && response.body()?.message == "success")
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                println("API Failure: ${t.message}")
                callback(false)
            }
        })
    }
}