package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.example.newsapp.NewsApplication
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.lang.Exception

//AndroidViewModel is same as ViewModel but we can use the application context inside it.

//If we have a constructor parameter for our view model then we'll have to define a view model factory.
class NewsViewModel(app: Application ,val newsRepository: NewsRepository):AndroidViewModel(app) {

    //Our fragments can subscribe to this livedata as observers and whenever we post changes to the livedata then our
    //observers will automatically get notified about the changes.
    //Even when we rotate our devices the UI is rebuilt but the viewmodel is not affected therefore we still get the up to date data.

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var breakingNewsPage = 1
    var searchNewsPage = 1

    var breakingNewsResponse:NewsResponse? = null
    var searchNewsResponse:NewsResponse? = null


    init {
        getBreakingNews("us")
    }


    //CoroutineScope tied to this ViewModel. This scope will be canceled when ViewModel will be cleared,
    // i.e ViewModel.onCleared is called
    fun getBreakingNews(countryCode:String) = viewModelScope.launch {
        safeBreakingNewsCall(countryCode)
    }

    fun getSearchedNews(searchQuery:String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }


    //Decides to emit the success or error state in the Resource class depending upon the response.
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                //if the body is not null then execute this block
                responseBody ->

                breakingNewsPage+=1  //which page to request for in the next request for breaking news
                if(breakingNewsResponse==null){
                    breakingNewsResponse=responseBody
                }
                else{
                    val oldArticles:MutableList<Article> = breakingNewsResponse!!.articles  //get the prev pages articles(REFERENCE)
                    val newArticles:MutableList<Article> = responseBody.articles  //get current requested page articles.
                    oldArticles.addAll(newArticles)  //append the latest page articles(same as breakingNewsResponse.articles.addAll(newsArticles)
                }


                return Resource.Success(breakingNewsResponse?:responseBody)
            }
        }

        return Resource.Error(response.message())
    }

    private fun handleSearchedNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                //if the body is not null then execute this block
                responseBody ->

                searchNewsPage+=1  //which page to request for in the next request
                if(searchNewsResponse==null){
                    searchNewsResponse=responseBody
                }
                else{
                    val oldArticles:MutableList<Article> = searchNewsResponse!!.articles  //get the prev pages articles
                    val newArticles:MutableList<Article> = responseBody.articles  //get current requested page articles.
                    oldArticles.addAll(newArticles)  //append the latest page articles
                }


                return Resource.Success(searchNewsResponse?:responseBody)
            }
        }

        return Resource.Error(response.message())
    }

    //Local db related queries

    //Since it's a read query to local db we don't require coroutine.
    fun getSavedNewsArticle():LiveData<List<Article>> =  newsRepository.getAllSavedArticles()

    fun upsert(article:Article) =  viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article:Article) =  viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    fun isArticleSavedAlready(articleUrl:String):LiveData<Long> = newsRepository.isArticleAlreadySaved(articleUrl)




    suspend fun safeBreakingNewsCall(countryCode: String){
        //emit the loading state before making the network call
        breakingNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                //Make the api request and get the result
                val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage) //after the result is obtained the next lines will be executed.

                breakingNews.postValue(handleBreakingNewsResponse(response))
            }
        }
        catch (e:Exception){
            when(e){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure Error"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    suspend fun safeSearchNewsCall(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val response = newsRepository.searchForNews(searchQuery,searchNewsPage)

                searchNews.postValue(handleSearchedNewsResponse(response))
            }
        }
        catch (e:Exception){
            when(e){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure Error"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private fun hasInternetConnection(): Boolean{
        //this function is only available inside of AndroidViewModel
        val connectivityManager= getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false //if it's null then return false.
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?:return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            //below android version 23 the below code is not deprecated
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}