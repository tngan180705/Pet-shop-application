package com.example.dacs3.model

data class OrderReview(
    val productId: Int,
    val productName: String,
    val rating: Int,
    val reviewDate: String,
    val reviewDescription: String,
    val productImage: String
)