package com.example.dacs3.controller

import com.example.dacs3.api.ResponseMessage
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.api.UserApi
import com.example.dacs3.model.Comment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommentController(private val userApi: UserApi) {
    fun addComment(comment: Comment, callback: (Boolean, String?) -> Unit) {
        println("Calling addComment with comment: $comment")
        userApi.addComment(comment).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                println("API Response: ${response.code()} - ${response.body()} - ErrorBody: ${response.errorBody()?.string()}")
                if (response.isSuccessful && response.body()?.message == "success") {
                    callback(true, null)
                } else {
                    val errorMessage = response.body()?.message ?: response.errorBody()?.string() ?: "Lỗi không xác định"
                    callback(false, errorMessage)
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                println("API Failure: ${t.message ?: "Unknown error"}")
                callback(false, t.message ?: "Lỗi kết nối mạng")
            }
        })
    }

    fun getComments(productId: Int, callback: (List<Comment>) -> Unit) {
        println("Fetching comments for productId: $productId")
        userApi.getComments(productId).enqueue(object : Callback<ResponseMessageWrapper<List<Comment>>> {
            override fun onResponse(
                call: Call<ResponseMessageWrapper<List<Comment>>>,
                response: Response<ResponseMessageWrapper<List<Comment>>>
            ) {
                println("API Response: ${response.code()} - ${response.body()}")
                if (response.isSuccessful) {
                    callback(response.body()?.data ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseMessageWrapper<List<Comment>>>, t: Throwable) {
                println("API Failure: ${t.message ?: "Unknown error"}")
                callback(emptyList())
            }
        })
    }
}