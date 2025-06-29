package com.example.news26.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.news26.data.Source
import java.io.Serializable

@Entity(tableName = "articles")
data class RoomArticle(
    @PrimaryKey val url: String,
val author: String,
val content: String,
val description: String,
val publishedAt: String,
val source: Source,
val title: String,
val urlToImage: String
): Serializable

