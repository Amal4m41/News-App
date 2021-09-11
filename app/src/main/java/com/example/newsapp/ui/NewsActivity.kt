package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.NewsApplication
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityNewsBinding
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.repository.NewsRepository

class NewsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityNewsBinding
    lateinit var viewModel:NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val articleDatabase:ArticleDatabase = ArticleDatabase(this)
        val newsRepository = NewsRepository(articleDatabase)
        val viewModelProviderFactory = NewsViewModelProviderFactory(application,newsRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory).get(NewsViewModel::class.java)

        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Connecting our bottomNavigationView(menu) with navigation components(the fragment container)
        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.newsNavHostFragment))

    }
}