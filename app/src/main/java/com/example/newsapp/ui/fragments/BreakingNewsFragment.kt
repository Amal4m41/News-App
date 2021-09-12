package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
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

                        if(binding.llNoInternetMsg.visibility==View.VISIBLE){
                            binding.apply {
                                llNoInternetMsg.visibility=View.GONE
                                rvBreakingNews.visibility=View.VISIBLE
                            }
                        }
//                        Toast.makeText(activity, "SUCCESS $newsAdapter", Toast.LENGTH_SHORT).show()
                        newsAdapter.differ.submitList(newsResponse.articles.toList())  //passing the articles list to the adapter
//                        Toast.makeText(activity, "${newsAdapter.itemCount}", Toast.LENGTH_SHORT).show()


                        //Every time we get a response we check if it's the last page so that we can stop the pagination.
                        //We get the overall total articles available then divide it by 20 + 1(cuz int division) to get the total pages
                        val totalPages: Int = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 1
                        isLastPage = viewModel.breakingNewsPage - 1 == totalPages  //- 1 cuz the last page is always empty
                        if (isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                            //once the last page is reached we don't require space for the pagination loading progress bar
                        }
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Log.e(TAG, "Error : $message")


                        if(message==Constants.NO_INTERNET_CONNECTION && viewModel.hasInternetConnection()){
                            //if previously we didn't have internet connection and now when the fragment is chosen the
                            //internet is active, then make request for breaking news.
                            //Otherwise the user will have to restart the app to get the breaking news, since it's only called
                            //when the viewmodel object is created.

                            viewModel.breakingNewsPage=1
                            viewModel.breakingNewsResponse=null
                            viewModel.getBreakingNews("us")

                        }
                        else{
                            if(message==Constants.NO_INTERNET_CONNECTION){
                                binding.apply {
                                    rvBreakingNews.visibility=View.INVISIBLE
                                    tvNoInternetMsg.text=message
                                    llNoInternetMsg.visibility=View.VISIBLE
                                }
                            }
                            else{
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
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
        isLoading=false
    }

    private fun showProgressBar(){
        binding.paginationProgressBarBreakingNews.visibility = View.VISIBLE
        isLoading=true
    }


    //Pagination related properties
    var isLoading=false
    var isLastPage=false
    var isScrolling=false


    //     * An OnScrollListener can be added to a RecyclerView to receive messages when a scrolling event
    //     * has occurred on that RecyclerView.
    val scrollListener = object: RecyclerView.OnScrollListener(){

        //Callback method to be invoked when RecyclerView's scroll state changes.
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
            {
                isScrolling=true
            }
        }

        //Callback method to be invoked when the RecyclerView has been scrolled. This will be called after the scroll has completed.
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            //To find if we have reached the end of a page(w.r.t the number of articles) after scrolling
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition() //position of the item that's first visible on our SCREEN
            val visibleItemCount = layoutManager.childCount //tells how many items are visible in the current screen.
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition > 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible
                    && isScrolling

            if(shouldPaginate){
                viewModel.getBreakingNews("us")
                isScrolling=false
            }

        }
    }

    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()  //no list is passed
        binding.rvBreakingNews.apply {
            layoutManager= LinearLayoutManager(activity)
            adapter=newsAdapter
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}