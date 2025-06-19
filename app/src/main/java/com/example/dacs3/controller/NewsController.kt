package com.example.dacs3.controller

import com.example.dacs3.api.ApiClient
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.model.NewsArticle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsController {
    private val userApi = ApiClient.userApi

    fun getAllNews(callback: (List<NewsArticle>) -> Unit) {
        userApi.getAllNews().enqueue(object : Callback<ResponseMessageWrapper<List<NewsArticle>>> {
            override fun onResponse(
                call: Call<ResponseMessageWrapper<List<NewsArticle>>>,
                response: Response<ResponseMessageWrapper<List<NewsArticle>>>
            ) {
                if (response.isSuccessful && response.body()?.data != null) {
                    callback(response.body()!!.data!!)
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseMessageWrapper<List<NewsArticle>>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun getNewsDetail(id: Int, callback: (NewsArticle?) -> Unit) {
        userApi.getNewsDetail(id).enqueue(object : Callback<ResponseMessageWrapper<NewsArticle>> {
            override fun onResponse(
                call: Call<ResponseMessageWrapper<NewsArticle>>,
                response: Response<ResponseMessageWrapper<NewsArticle>>
            ) {
                if (response.isSuccessful && response.body()?.data != null) {
                    callback(response.body()!!.data)
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<ResponseMessageWrapper<NewsArticle>>, t: Throwable) {
                callback(null)
            }
        })
    }
}