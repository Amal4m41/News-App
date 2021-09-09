package com.example.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val breakingNewsPage = 1
    val searchNewsPage = 1


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
        val response = newsRepository.searchForNews(searchQuery,breakingNewsPage)

        searchNews.postValue(handleSearchedNewsResponse(response))
    }


    //Decides to emit the success or error state in the Resource class depending upon the response.
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                //if the body is not null then execute this block
                responseBody ->
                return Resource.Success(responseBody)
            }
        }

        return Resource.Error(response.message())
    }

    private fun handleSearchedNewsResponse(response: Response<NewsResponse>):Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                //if the body is not null then execute this block
                responseBody ->
                return Resource.Success(responseBody)
            }
        }

        return Resource.Error(response.message())
    }



}