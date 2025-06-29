package com.example.news26.repository

import com.example.news26.api.RetrofitInstance
import com.example.news26.db.ArticleDatabase
import com.example.news26.db.RoomArticle

// view model

class NewsRepository(val db : ArticleDatabase) {

    suspend fun getHeadLines(country:String,pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(country,pageNumber)

    suspend fun upsert(article: RoomArticle) = db.getDao().insert(article)

    suspend fun delete(article: RoomArticle) = db.getDao().delete(article)

    fun getAllFav() = db.getDao().getAllfav()

}