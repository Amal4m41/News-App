package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import retrofit2.Response


//We access data from api and local db(saved articles)
//The job of repository is to get data from the local db and the remote source(api), in the repository we'll have functions that'll
//directly query for the data and then in the newsViewModel we have the instance of the repository so that within the viewmodel
//we can call the functions in the repository and then we'll have livedata objects that'll notify all of our fragments about the
//change in data
class NewsRepository(val db:ArticleDatabase) {

    //Getting data from the api requests:
    suspend fun getBreakingNews(countryCode:String,pageNumber:Int): Response<NewsResponse> =
            RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchForNews(query: String,pageNumber: Int): Response<NewsResponse> =
            RetrofitInstance.api.searchForNews(query,pageNumber)

    
//    suspend fun upsert(article:Article) = db.getArticleDao().upsertArticle(article)
}