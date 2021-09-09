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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.models.Article
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Resource
import java.util.*

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {

    private var _binding:FragmentBreakingNewsBinding?=null
    private val binding:FragmentBreakingNewsBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter:NewsAdapter

    val TAG = "BreakingNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBreakingNewsBinding.bind(view)

        setupRecyclerView()


        newsAdapter.setOnclickListener(fun(article:Article):Unit{
            //here we get the article we clicked from the recycler view, now we'll get the clicked article
            //put it in a bundle and then attach this bundle to the navigation component so that navigation
            //components will handle the navigation transition for us and pass the arguments to the article fragment.
            val bundle = Bundle()
            bundle.apply {
                putSerializable(Constants.ARTICLE,article)
            }

            //we need to pass the id of the action that makes the transition from this fragment to the article fragment.
            //and the arguments that is to be passed to the article fragment.
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment,bundle)

        })


        //get the instance value of viewmodel from the newsactivity
        viewModel = (activity as NewsActivity).viewModel

        //subscribing to all the changes w.r.t the livedata
        viewModel.breakingNews.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    //if the data is not null the update the recycler view list
                    it.data?.let { newsResponse ->
//                        Toast.makeText(activity, "SUCCESS $newsAdapter", Toast.LENGTH_SHORT).show()
                        newsAdapter.differ.submitList(newsResponse.articles)  //passing the articles list to the adapter
//                        Toast.makeText(activity, "${newsAdapter.itemCount}", Toast.LENGTH_SHORT).show()
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

    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideProgressBar(){
        binding.paginationProgressBarBreakingNews.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBarBreakingNews.visibility = View.VISIBLE
    }


    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()  //no list is passed
        binding.rvBreakingNews.apply {
            layoutManager= LinearLayoutManager(activity)
            adapter=newsAdapter
        }
    }
}