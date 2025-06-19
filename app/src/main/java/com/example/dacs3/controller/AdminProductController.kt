package com.example.dacs3.controller

import com.example.dacs3.api.ApiClient
import com.example.dacs3.api.DeleteProductRequest
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductController {
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProductsFromApi()
    }

    fun getProducts(): List<Product> {
        return _products.value
    }

    fun loadProductsFromApi(onSuccess: () -> Unit = {}, onError: (String) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            _isLoading.value = true // Bắt đầu tải
            val call = ApiClient.userApi.getAllProducts()
            call.enqueue(object : Callback<List<Product>> {
                override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                    _isLoading.value = false // Kết thúc tải
                    if (response.isSuccessful && response.body() != null) {
                        val productList = response.body()!!
                        if (_products.value != productList) {
                            _products.value = productList
                            onSuccess()
                        }
                    } else {
                        val error = "Failed to load products: ${response.errorBody()?.string()}"
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                    _isLoading.value = false // Kết thúc tải khi lỗi
                    onError("Error loading products: ${t.message}")
                }
            })
        }
    }

    fun addProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.userApi.addProduct(product)
            call.enqueue(object : Callback<ResponseMessageWrapper<Void>> {
                override fun onResponse(
                    call: Call<ResponseMessageWrapper<Void>>,
                    response: Response<ResponseMessageWrapper<Void>>
                ) {
                    if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                        loadProductsFromApi(onSuccess, onError)
                    } else {
                        val error = response.body()?.message ?: "Lỗi không xác định khi thêm sản phẩm"
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessageWrapper<Void>>, t: Throwable) {
                    onError("Lỗi thêm sản phẩm: ${t.message}")
                }
            })
        }
    }

    fun updateProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.userApi.updateProduct(product)
            call.enqueue(object : Callback<ResponseMessageWrapper<Void>> {
                override fun onResponse(
                    call: Call<ResponseMessageWrapper<Void>>,
                    response: Response<ResponseMessageWrapper<Void>>
                ) {
                    if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                        loadProductsFromApi(onSuccess, onError)
                    } else {
                        val error = response.body()?.message ?: "Lỗi không xác định khi cập nhật sản phẩm"
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessageWrapper<Void>>, t: Throwable) {
                    onError("Lỗi cập nhật sản phẩm: ${t.message}")
                }
            })
        }
    }

    fun deleteProduct(productId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.userApi.deleteProduct(DeleteProductRequest(productId))
            call.enqueue(object : Callback<ResponseMessageWrapper<Void>> {
                override fun onResponse(
                    call: Call<ResponseMessageWrapper<Void>>,
                    response: Response<ResponseMessageWrapper<Void>>
                ) {
                    if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                        loadProductsFromApi(onSuccess, onError)
                    } else {
                        val error = response.body()?.message ?: "Lỗi không xác định khi xóa sản phẩm"
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessageWrapper<Void>>, t: Throwable) {
                    onError("Lỗi xóa sản phẩm: ${t.message}")
                }
            })
        }
    }
}