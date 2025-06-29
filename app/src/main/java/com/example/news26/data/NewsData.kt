package com.example.news26.data

data class NewsData(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)