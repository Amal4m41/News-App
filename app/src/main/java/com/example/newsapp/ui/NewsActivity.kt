package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding:ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Connecting our bottomNavigationView(menu) with navigation components(the fragment container)
        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.newsNavHostFragment))


    }
}