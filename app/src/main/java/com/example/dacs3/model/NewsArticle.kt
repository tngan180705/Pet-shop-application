package com.example.dacs3.model

data class NewsArticle(
    val id: Int,
    val title: String,
    val summary: String,
    val date: String,
    val imageUrl: String = "",
    val content: String = ""
)
