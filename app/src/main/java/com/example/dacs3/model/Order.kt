package com.example.dacs3.model

data class Order(
    val idorder: Int,
    val id: Int, // ID người dùng
    val status: String,
    val orderdate: String,
    val total: Double,
    val details: List<OrderDetail>
)
