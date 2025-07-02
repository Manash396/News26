package com.example.news26

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.news26.data.NewsData
import com.example.news26.db.RoomArticle
import com.example.news26.repository.NewsRepository
import com.example.news26.util.Resource
import org.json.JSONObject
import kotlinx.coroutines.launch
import okio.IOException

import retrofit2.Response

class NewsViewModel(app: Application, private val newsRepository: NewsRepository) : AndroidViewModel(app) {
//  for pagination on scrolling to the last page

    val headLines: MutableLiveData<Resource<NewsData>> = MutableLiveData()
    var headLinePage = 1
    var headLineResponse : NewsData? =null
// evaluating the response
    private fun headLinesResponse(response : Response<NewsData>) : Resource<NewsData>{
        if (response.isSuccessful){
              headLinePage++
            response.body()?.let { resultResponse ->
                if (headLineResponse == null){
                    headLineResponse = resultResponse
                }else{
                    val oldArticles = headLineResponse?.articles?.toMutableList()
                     val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)

                    headLineResponse = headLineResponse?.copy(articles = oldArticles!!.toList())
                }
                 return Resource.Success(headLineResponse ?: resultResponse)
            }
        }
//           i have to create maually json object for error
        val errorObj = JSONObject(response.errorBody()!!.charStream().readText())

        return Resource.Error(message = errorObj.getString("message"))
    }


    fun addToFav(article: RoomArticle) = viewModelScope.launch {
       newsRepository.upsert(article)
    }

    fun getFavNews()  = newsRepository.getAllFav()

    fun deleteArticle(article: RoomArticle) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun internetConnection(context: Context):Boolean{
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }

            } ?: false
        }
    }

    private suspend fun headLinesInternet(countryCode : String){
        headLines.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())){
                val response = newsRepository.getHeadLines(countryCode,headLinePage)
                headLines.postValue(headLinesResponse(response))
            }else{
                headLines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t: Throwable){
           when(t) {
               is IOException -> headLines.postValue(Resource.Error(t.message.toString()))
               else -> headLines.postValue(Resource.Error("No signal"))
           }
        }
    }

    fun getHeadLines(countryCode: String)  = viewModelScope.launch {
        headLinesInternet(countryCode)
    }

    init {
        getHeadLines("us")
    }

}