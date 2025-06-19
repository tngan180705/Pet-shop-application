
package com.example.dacs3.controller

import com.example.dacs3.api.ApiClient
import com.example.dacs3.api.DeleteOrderRequest
import com.example.dacs3.api.ResponseMessage
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.api.UpdateOrderRequest
import com.example.dacs3.api.AddOrderRequest
import com.example.dacs3.model.Order
import com.example.dacs3.model.OrderDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderController {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val api = ApiClient.userApi

    init {
        fetchOrders()
    }

    fun getOrders(): List<Order> {
        return _orders.value
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setError(error: String) {
        _errorMessage.value = error
    }

    fun fetchOrders(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val call = api.getAllOrders()
            call.enqueue(object : Callback<ResponseMessageWrapper<List<Order>>> {
                override fun onResponse(
                    call: Call<ResponseMessageWrapper<List<Order>>>,
                    response: Response<ResponseMessageWrapper<List<Order>>>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.data != null) {
                        val orderList = response.body()!!.data!!
                        if (_orders.value != orderList) {
                            _orders.value = orderList
                            println("Đã tải đơn hàng: ${orderList.size}")
                            onSuccess()
                        }
                    } else {
                        val error = "Không thể tải danh sách đơn hàng: ${response.body()?.message ?: response.errorBody()?.string()}"
                        _errorMessage.value = error
                        println("Lỗi tải đơn hàng: $error")
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessageWrapper<List<Order>>>, t: Throwable) {
                    _isLoading.value = false
                    val error = "Lỗi tải đơn hàng: ${t.message}"
                    _errorMessage.value = error
                    println("Thất bại khi tải đơn hàng: $error")
                    onError(error)
                }
            })
        }
    }


    fun updateOrder(order: Order, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = UpdateOrderRequest(
                    idorder = order.idorder,
                    user_id = order.id,
                    orderdate = order.orderdate,
                    status = order.status,
                    total = order.total
                )
                val call = api.updateOrder(request)
                call.enqueue(object : Callback<ResponseMessage> {
                    override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                        _isLoading.value = false
                        if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                            println("Cập nhật đơn hàng thành công: idorder=${order.idorder}")
                            fetchOrders(onSuccess, onError)
                        } else {
                            val error = response.body()?.message ?: "Lỗi không xác định khi cập nhật đơn hàng"
                            _errorMessage.value = error
                            println("Lỗi cập nhật đơn hàng: $error")
                            onError(error)
                        }
                    }

                    override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                        _isLoading.value = false
                        val error = "Lỗi cập nhật đơn hàng: ${t.message}"
                        _errorMessage.value = error
                        println("Thất bại khi cập nhật đơn hàng: $error")
                        onError(error)
                    }
                })
            } catch (e: Exception) {
                _isLoading.value = false
                val error = "Lỗi khi cập nhật đơn hàng: ${e.message}"
                _errorMessage.value = error
                println("Ngoại lệ khi cập nhật đơn hàng: $error")
                onError(error)
            }
        }
    }

    fun deleteOrder(orderId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val call = api.deleteOrder(DeleteOrderRequest(orderId))
            call.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                        println("Xóa đơn hàng thành công: idorder=$orderId")
                        fetchOrders(onSuccess, onError)
                    } else {
                        val error = response.body()?.message ?: "Lỗi không xác định khi xóa đơn hàng"
                        _errorMessage.value = error
                        println("Lỗi xóa đơn hàng: $error")
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    _isLoading.value = false
                    val error = "Lỗi xóa đơn hàng: ${t.message}"
                    _errorMessage.value = error
                    println("Thất bại khi xóa đơn hàng: $error")
                    onError(error)
                }
            })
        }
    }
}
