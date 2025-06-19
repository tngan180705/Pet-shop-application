package com.example.dacs3.controller

import com.example.dacs3.api.UserApi
import com.example.dacs3.model.CategoryType
import com.example.dacs3.model.Product
import com.example.dacs3.model.SubCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeController(private val userApi: UserApi) {
    fun getCategories(callback: (Map<CategoryType, List<SubCategory>>) -> Unit) {
        userApi.getCategories().enqueue(object : Callback<List<CategoryType>> {
            override fun onResponse(call: Call<List<CategoryType>>, response: Response<List<CategoryType>>) {
                if (response.isSuccessful) {
                    val categories = response.body() ?: emptyList()
                    val categoryMap = categories.associate { category ->
                        category to category.subcategories
                    }
                    callback(categoryMap)
                } else {
                    callback(emptyMap())
                }
            }

            override fun onFailure(call: Call<List<CategoryType>>, t: Throwable) {
                callback(emptyMap())
            }
        })
    }

    fun getProductsBySubCategory(subCategoryId: Int, callback: (List<Product>) -> Unit) {
        userApi.getProductsBySubCategory(subCategoryId).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    callback(response.body() ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getFeaturedProducts(callback: (List<Product>) -> Unit) {
        userApi.getFeaturedProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    callback(response.body() ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getBestSellingProducts(callback: (List<Product>) -> Unit) {
        userApi.getBestSellingProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    callback(response.body() ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }
    fun getAllProducts(callback: (List<Product>) -> Unit) {
        userApi.getAllProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    callback(response.body() ?: emptyList())
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