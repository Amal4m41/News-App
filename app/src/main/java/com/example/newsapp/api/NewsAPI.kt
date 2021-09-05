package com.example.newsapp.api


import com.example.newsapp.ui.NewsResponse
import com.example.newsapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

//Interface used to define our requests to the api
interface NewsAPI {

    @GET("v2/top-headlines")  //url path to get the breaking news
    suspend fun getBreakingNews(
        @Query("country")        //defining the query parameters
        countryCode:String = "us",
        @Query("page")          //useful for pagination(by default pageSize is 20, i.e. 20 results per page)
        pageNumber:Int =1,
        @Query("apiKey")
        apiKey:String = Constants.API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")  //url path for searching news
    suspend fun searchForNews(
        @Query("q")        //defining the query parameters
        searchQuery:String,
        @Query("page")          //useful for pagination(by default pageSize is 20, i.e. 20 results per page)
        pageNumber:Int =1,
        @Query("apiKey")
        apiKey:String = Constants.API_KEY
    ): Response<NewsResponse>

}