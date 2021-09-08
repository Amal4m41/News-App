package com.example.newsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.newsapp.R
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    private lateinit var viewModel:NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get the value of the view model instance from the activity holding this fragment.
        viewModel = (activity as NewsActivity).viewModel

    }
    

}