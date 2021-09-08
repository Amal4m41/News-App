package com.example.newsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.newsapp.R
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

    private lateinit var viewModel:NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = (activity as NewsActivity).viewModel
    }
}