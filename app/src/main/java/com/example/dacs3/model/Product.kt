package com.example.dacs3.model

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrls: List<String>? // Change to nullable
)