package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {


    private var _binding: FragmentSearchNewsBinding?=null
    private val binding: FragmentSearchNewsBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    val TAG = "SearchNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchNewsBinding.bind(view)

        setupRecyclerView()

        //get the instance value of viewmodel from the newsactivity
        viewModel = (activity as NewsActivity).viewModel

        //subscribing to all the changes w.r.t the livedata
        viewModel.searchNews.observe(viewLifecycleOwner, {
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


        //Adding searching feature with time delay of 500L, this can be easily achieved using coroutines.
        var job: Job? = null
        binding.etSearchNews.addTextChangedListener {
            editable ->

            job?.cancel()  //if it's not null and the user has made some change in text within 500ms, then cancel the previous job.
            job = MainScope().launch {
                delay(500L) //wait for 500 ms for the user to type before firing the request.(otherwise there'll be too many requests)
                editable?.let {
                    //if editable is not null then check if it's empty, if not then fire request.
                    if(editable.toString().isNotEmpty()){
                        viewModel.getSearchedNews(editable.toString()) //this will fire the request and update the livedata searchNews
                        //which is subscribed by this fragment.
                    }
                }
            }
        }

    }

    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }


    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()  //no list is passed
        binding.rvSearchNews.apply {
            layoutManager= LinearLayoutManager(activity)
            adapter=newsAdapter
        }
    }
    

}