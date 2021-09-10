package com.example.newsapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response


//If we have a constructor parameter for our view model then we'll have to define a view model factory.
class NewsViewModel(val newsRepository: NewsRepository):ViewModel() {

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
        //emit the loading state before making the network call
        breakingNews.postValue(Resource.Loading())

        //Make the api request and get the result
        val response = newsRepository.getBreakingNews(countryCode,breakingNewsPage) //after the result is obtained the next lines will be executed.

        breakingNews.postValue(handleBreakingNewsResponse(response))
    }

    fun getSearchedNews(searchQuery:String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchForNews(searchQuery,searchNewsPage)

        searchNews.postValue(handleSearchedNewsResponse(response))
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
}