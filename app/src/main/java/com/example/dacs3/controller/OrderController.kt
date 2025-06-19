package com.example.dacs3.controller

import com.example.dacs3.api.ApiClient
import com.example.dacs3.api.ResponseMessage
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.model.CartItem
import com.example.dacs3.model.Order
import com.example.dacs3.model.OrderRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderController {

    fun createOrder(phone: String, cartItems: List<CartItem>, total: Double, callback: (String) -> Unit) {
        val orderRequest = OrderRequest(phone, cartItems, total)
        val call = ApiClient.userApi.createOrder(orderRequest)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val message = result?.message ?: "Đặt hàng thất bại!"
                    if (message.contains("Lỗi", ignoreCase = true) || message.contains("thất bại", ignoreCase = true)) {
                        callback("Lỗi đặt hàng: $message")
                    } else {
                        callback(message)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Không có thông tin lỗi từ server"
                    callback("Lỗi ${response.code()}: $errorMessage")
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                callback("Lỗi kết nối: ${t.message}")
            }
        })
    }

    fun getOrders(phone: String, callback: (List<Order>) -> Unit) {
        val call = ApiClient.userApi.getOrders(phone)
        call.enqueue(object : Callback<ResponseMessageWrapper<List<Order>>> {
            override fun onResponse(call: Call<ResponseMessageWrapper<List<Order>>>, response: Response<ResponseMessageWrapper<List<Order>>>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    callback(result?.data ?: emptyList())
                } else {
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<ResponseMessageWrapper<List<Order>>>, t: Throwable) {
                callback(emptyList())
            }
        })
    }

    fun updateOrderStatus(idorder: Int, status: String, callback: (String) -> Unit) {
        val data = mapOf("idorder" to idorder, "status" to status)
        val call = ApiClient.userApi.updateOrderStatus(data)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    callback(result?.message ?: "Cập nhật thất bại!")
                } else {
                    callback("Lỗi ${response.code()}: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                callback("Lỗi kết nối: ${t.message}")
            }
        })
    }
}