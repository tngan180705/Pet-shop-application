package com.example.dacs3.model

data class CategoryType(
    val idctg: Int,
    val namectg: String,
    val subcategories: List<SubCategory>
)