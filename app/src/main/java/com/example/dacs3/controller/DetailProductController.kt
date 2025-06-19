package com.example.dacs3.controller

import com.example.dacs3.api.UserApi
import com.example.dacs3.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProductController(private val userApi: UserApi) {

    fun getProductDetail(productId: Int, callback: (Product?) -> Unit) {
        userApi.getProductDetail(productId).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    callback(response.body())
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                callback(null)
            }
        })
    }

    fun getRelatedProducts(productId: Int, callback: (List<Product>) -> Unit) {
        userApi.getRelatedProducts(productId).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    callback(response.body()?.take(4) ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
}