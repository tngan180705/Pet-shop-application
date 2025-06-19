package com.example.dacs3.controller

import com.example.dacs3.api.ApiClient
import com.example.dacs3.api.ResponseMessage
import com.example.dacs3.api.UserCredentials
import com.example.dacs3.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserController {
    private val users = mutableListOf<User>()
    private var nextId = 1

    init {
        users.add(User(nextId++, "tngan", "0566699305", "123", "tngan@example.com", "119/33 phạm như xương"))
        users.add(User(nextId++, "uyen", "0912123305", "123", "uyen@example.com", "119/33 phạm như xương"))
    }

    fun registerUser(user: User, callback: (String) -> Unit) {
        if (user.phone.length != 10) {
            callback("Số điện thoại phải có 10 số!")
            return
        }
        if (user.username.isEmpty() || user.password.isEmpty()) {
            callback("Vui lòng điền đầy đủ thông tin!")
            return
        }

        val call = ApiClient.userApi.registerUser(user)
        call.enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    callback(response.body()?.message ?: "Đăng ký thành công!")
                } else {
                    callback("Lỗi: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                callback("Lỗi kết nối: ${t.message}")
            }
        })
    }

    fun loginUser(phone: String, password: String, callback: (String, User?) -> Unit) {
        val credentials = UserCredentials(phone, password)
        println("Sending login credentials to API: phone=$phone, password=$password")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.userApi.loginUser(credentials).execute()
                withContext(Dispatchers.Main) {
                    println("Login API response code: ${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val message = responseBody?.message ?: "Lỗi xảy ra!"
                        println("Login API response body: $message")
                        if (responseBody?.message == "Đăng nhập thành công!") {
                            val user = responseBody.user
                            if (user != null) {
                                // Cập nhật danh sách users
                                val index = users.indexOfFirst { it.phone == phone }
                                if (index != -1) {
                                    users[index] = user
                                } else {
                                    users.add(user)
                                }
                                callback(message, user)
                            } else {
                                // Nếu API không trả về user, thử gọi fetchUserFromApi
                                fetchUserFromApi(phone) { fetchedUser ->
                                    if (fetchedUser != null) {
                                        val index = users.indexOfFirst { it.phone == phone }
                                        if (index != -1) {
                                            users[index] = fetchedUser
                                        } else {
                                            users.add(fetchedUser)
                                        }
                                        callback("Đăng nhập thành công!", fetchedUser)
                                    } else {
                                        callback("Lỗi: Không tìm thấy thông tin người dùng", null)
                                    }
                                }
                            }
                        } else {
                            callback("Lỗi: $message", null)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Phản hồi không thành công"
                        callback("Lỗi: $errorBody", null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Login API failure: ${e.message}")
                    callback("Lỗi kết nối: ${e.message}", null)
                }
            }
        }
    }

    fun fetchUserFromApi(phone: String, callback: (User?) -> Unit) {
        println("Fetching user from API with phone: $phone")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.userApi.getUserByPhone(phone).execute()
                withContext(Dispatchers.Main) {
                    println("Fetch user API response code: ${response.code()}")
                    if (response.isSuccessful) {
                        val user = response.body()?.user
                        println("Fetch user API response body: ${response.body()?.message}")
                        if (user != null) {
                            val index = users.indexOfFirst { it.phone == phone }
                            if (index != -1) {
                                users[index] = user
                            } else {
                                users.add(user)
                            }
                        }
                        callback(user)
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        println("Fetch user API error body: $errorBody")
                        callback(null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Fetch user API failure: ${e.message}")
                    callback(null)
                }
            }
        }
    }

    // Thêm phương thức getUserInfo để gọi API và trả về callback phù hợp
    fun getUserInfo(phone: String, callback: (User?, String) -> Unit) {
        println("Getting user info from API with phone: $phone")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.userApi.getUserByPhone(phone).execute()
                withContext(Dispatchers.Main) {
                    println("Get user info API response code: ${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val message = responseBody?.message ?: "Lỗi xảy ra!"
                        println("Get user info API response body: $message")
                        if (responseBody?.message == "Lấy thông tin thành công!") {
                            val user = responseBody.user
                            if (user != null) {
                                // Cập nhật danh sách users
                                val index = users.indexOfFirst { it.phone == phone }
                                if (index != -1) {
                                    users[index] = user
                                } else {
                                    users.add(user)
                                }
                                callback(user, message)
                            } else {
                                callback(null, "Không tìm thấy thông tin người dùng")
                            }
                        } else {
                            callback(null, message)
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Phản hồi không thành công"
                        callback(null, "Lỗi: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Get user info API failure: ${e.message}")
                    callback(null, "Lỗi kết nối: ${e.message}")
                }
            }
        }
    }

    fun updateUser(phone: String, newUser: User, callback: (String) -> Unit) {
        println("Updating user with phone: $phone, newUser: $newUser")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.userApi.updateUser(newUser).execute()
                withContext(Dispatchers.Main) {
                    println("Update API response code: ${response.code()}")
                    println("Update API response body: ${response.body()}")
                    println("Update API error body: ${response.errorBody()?.string()}")

                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody?.message == "Cập nhật thông tin thành công!") {
                            val index = users.indexOfFirst { it.phone == phone }
                            if (index != -1) {
                                users[index] = newUser.copy(id = responseBody.user?.id ?: newUser.id)
                            } else {
                                users.add(newUser)
                            }
                            callback("Cập nhật thông tin thành công!")
                        } else {
                            callback(responseBody?.message ?: "Cập nhật thất bại!")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        callback("Lỗi: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Update API failure: ${e.message}")
                    callback("Lỗi kết nối: ${e.message}")
                }
            }
        }
    }

    fun getUser(phone: String): User? {
        return users.find { it.phone == phone }
    }
}