package com.example.dacs3.model

data class User(
    val id: Int,
    val username: String,
    val phone: String,
    val password: String,
    val email: String? = null,
    val address: String? = null, // Thay đổi thành nullable
    val profilePicture: String? = null,
    val isAdmin: Int = 0
)