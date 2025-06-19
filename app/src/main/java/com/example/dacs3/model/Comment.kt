package com.example.dacs3.model

data class Comment(
    val idcmt: Int = 0,
    val idsp: Int,
    val id: Int,
    val rate: Int,
    val description: String,
    val username: String? = null
)