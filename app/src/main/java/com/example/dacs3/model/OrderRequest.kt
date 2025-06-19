package com.example.dacs3.model

data class OrderRequest(
    val phone: String,
    val cartItems: List<CartItem>,
    val total: Double
)