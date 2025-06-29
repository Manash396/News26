package com.example.news26.api

import com.example.news26.data.NewsData
import com.example.news26.util.Constant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi  {

@GET("v2/top-headlines")
suspend fun getHeadlines(
    @Query("country")
    countryCode: String = "us",
    @Query("page")
    pageNumber : Int = 1,
    @Query("apiKey")
    apiKey: String  = Constant.API_KEY
) : Response<NewsData>

}