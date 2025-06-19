package com.example.dacs3.controller

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.dacs3.api.ApiClient
import com.example.dacs3.api.DeleteUserRequest
import com.example.dacs3.api.ResponseMessage
import com.example.dacs3.api.ResponseMessageWrapper
import com.example.dacs3.model.Admin
import com.example.dacs3.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AdminController {
    fun getMenuItems(): List<Admin> {
        return listOf(
            Admin("Quản lý tài khoản"),
            Admin("Quản lý tin tức"),
            Admin("Quản lý sản phẩm"),
            Admin("Quản lý đơn hàng")
        )
    }

    private var _users by mutableStateOf<List<User>>(emptyList())

    fun getUsers(): List<User> = _users

    fun addUser(user: User) {
        val nextId = (_users.maxOfOrNull { it.id } ?: 0) + 1
        _users = _users + user.copy(id = nextId)
    }

    fun addUserToApi(user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.userApi.registerUser(user)
            call.enqueue(object : Callback<ResponseMessage> {
                override fun onResponse(
                    call: Call<ResponseMessage>,
                    response: Response<ResponseMessage>
                ) {
                    if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                        // Tải lại danh sách người dùng từ API để đảm bảo đồng bộ
                        loadUsersFromApi(
                            onUsersLoaded = { userList ->
                                println("Reloaded users after adding: $userList")
                                onSuccess()
                            },
                            onError = { error ->
                                println("Error reloading users after adding: $error")
                                onError(error)
                            }
                        )
                    } else {
                        val error = "Failed to add user: ${response.errorBody()?.string()}"
                        println(error)
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                    val error = "Error adding user: ${t.message}"
                    println(error)
                    onError(error)
                }
            })
        }
    }

    fun deleteUser(userId: Int) {
        _users = _users.filterNot { it.id == userId }
    }

    fun updateUser(updatedUser: User) {
        _users = _users.map { if (it.id == updatedUser.id) updatedUser else it }
    }

    fun searchUsers(query: String): List<User> {
        return if (query.isEmpty()) {
            _users
        } else {
            _users.filter { it.username.contains(query, ignoreCase = true) }
        }.also {
            println("Search result for query '$query': $it")
        }
    }

    fun loadUsersFromApi(onUsersLoaded: (List<User>) -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.userApi.getAllUsers()
            call.enqueue(object : Callback<ResponseMessageWrapper<List<User>>> {
                override fun onResponse(
                    call: Call<ResponseMessageWrapper<List<User>>>,
                    response: Response<ResponseMessageWrapper<List<User>>>
                ) {
                    println("API Response: ${response.code()} - ${response.body()}")
                    if (response.isSuccessful && response.body()?.data != null) {
                        val userList = response.body()?.data ?: emptyList()
                        println("Loaded users from API: $userList")
                        _users = userList
                        onUsersLoaded(userList)
                    } else {
                        val error = "Failed to load users: ${response.errorBody()?.string()}"
                        println(error)
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessageWrapper<List<User>>>, t: Throwable) {
                    val error = "Error loading users: ${t.message}"
                    println(error)
                    onError(error)
                }
            })
        }
    }

    fun deleteUserFromApi(userId: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val call = ApiClient.userApi.deleteUser(DeleteUserRequest(userId))
            call.enqueue(object : Callback<ResponseMessageWrapper<Void>> {
                override fun onResponse(
                    call: Call<ResponseMessageWrapper<Void>>,
                    response: Response<ResponseMessageWrapper<Void>>
                ) {
                    if (response.isSuccessful && response.body()?.message?.contains("thành công") == true) {
                        deleteUser(userId)
                        onSuccess()
                    } else {
                        val error = "Failed to delete user: ${response.errorBody()?.string()}"
                        println(error)
                        onError(error)
                    }
                }

                override fun onFailure(call: Call<ResponseMessageWrapper<Void>>, t: Throwable) {
                    val error = "Error deleting user: ${t.message}"
                    println(error)
                    onError(error)
                }
            })
        }
    }
}