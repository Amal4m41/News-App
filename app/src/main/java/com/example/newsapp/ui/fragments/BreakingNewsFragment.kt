package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.ActivityNewsBinding
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.util.Resource
import java.util.*

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    private lateinit var rvBreakingNews:RecyclerView
    private lateinit var paginationProgressBar:ProgressBar

    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter:NewsAdapter
//    private lateinit var binding:FragmentBreakingNewsBinding

    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding = FragmentBreakingNewsBinding.inflate(layoutInflater)
        rvBreakingNews = view.findViewById(R.id.rvBreakingNews)
        paginationProgressBar = view.findViewById(R.id.paginationProgressBarBreakingNews)

        viewModel = (activity as NewsActivity).viewModel
        setupRecyclerView()

        //subscribing to all the changes w.r.t the livedata
        viewModel.breakingNews.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    //if the data is not null the update the recycler view list
                    it.data?.let { newsResponse ->
                        Toast.makeText(activity, "SUCCESS $newsAdapter", Toast.LENGTH_SHORT).show()
                        newsAdapter.differ.submitList(newsResponse.articles)
                        Toast.makeText(activity, "${newsAdapter.itemCount}", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Log.e(TAG, "Error $message")
                    }

                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })

    }

    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
    }


    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()
//        binding.rvBreakingNews.apply {
//            layoutManager= LinearLayoutManager(activity)
//            adapter=newsAdapter
//        }

        rvBreakingNews.layoutManager = LinearLayoutManager(activity)
        rvBreakingNews.adapter=newsAdapter
//        Toast.makeText(activity, binding.rvBreakingNews.adapter.toString(), Toast.LENGTH_SHORT).show()
    }
}