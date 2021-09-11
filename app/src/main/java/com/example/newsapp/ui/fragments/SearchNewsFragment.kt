package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.models.Article
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
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

        newsAdapter.setOnclickListener(fun(article: Article):Unit{
            //here we get the article we clicked from the recycler view, now we'll get the clicked article
            //put it in a bundle and then attach this bundle to the navigation component so that navigation
            //components will handle the navigation transition for us and pass the arguments to the article fragment.
            val bundle = Bundle()
            bundle.apply {
                putSerializable(Constants.ARTICLE,article)
            }

            //we need to pass the id of the action that makes the transition from this fragment to the article fragment.
            //and the arguments that is to be passed to the article fragment.
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,bundle)

        })


        //get the instance value of viewmodel from the newsactivity
        viewModel = (activity as NewsActivity).viewModel

        //subscribing to all the changes w.r.t the livedata
        viewModel.searchNews.observe(viewLifecycleOwner, {

//            Toast.makeText(activity, "HERE AGAIN", Toast.LENGTH_SHORT).show()
            when (it) {
                is Resource.Success -> {
                    hideProgressBar()
                    //if the data is not null the update the recycler view list
                    it.data?.let { newsResponse ->
//                        if (newsResponse.articles.isNullOrEmpty()) {
//                            binding.tvSearchToFindMessage.apply {
//                                text = "No articles found! Try different keyword"
//                                visibility = View.VISIBLE
//                            }
//                            binding.ivSearchToFindImage.visibility = View.INVISIBLE
//                            binding.rvSearchNews.visibility = View.INVISIBLE
//                        } else {
//                            binding.ivSearchToFindImage.visibility = View.INVISIBLE
//                            binding.tvSearchToFindMessage.visibility = View.INVISIBLE
//                            binding.rvSearchNews.visibility = View.VISIBLE
                        Log.e(TAG, "Search success: $newsResponse ")
                        newsAdapter.differ.submitList(newsResponse.articles.toList())  //passing the articles list to the adapter


                        //Every time we get a response we check if it's the last page so that we can stop the pagination.
                        //We get the overall total articles available then divide it by 20 + 1(cuz int division) to get the total pages
                        val totalPages: Int = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 1
                        isLastPage = viewModel.searchNewsPage - 1 == totalPages  //- 1 cuz the last page is always empty
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                            //once the last page is reached we don't require space for the pagination loading progress bar
                        }


//                        }
//                        Toast.makeText(activity, "SUCCESS $newsAdapter", Toast.LENGTH_SHORT).show()
//                        Toast.makeText(activity, "${newsAdapter.itemCount}", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Log.e(TAG, "Error $message")
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
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

                        //Every time the query changes reset the previous pages/articles list and page count.
                        viewModel.searchNewsPage=1
                        viewModel.searchNewsResponse=null

                        viewModel.getSearchedNews(editable.toString()) //this will fire the request and update the livedata searchNews
                        //which is subscribed by this fragment.
//                        Log.e(TAG, "Search query: ${editable.toString()} ", )

                    }
                }
            }
        }

    }

    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
//        Toast.makeText(activity, "Destroy", Toast.LENGTH_SHORT).show()
        _binding = null
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
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

//        Callback method to be invoked when the RecyclerView has been scrolled. This will be called after the scroll has completed.
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
                viewModel.getSearchedNews(binding.etSearchNews.text.toString())
                isScrolling=false
            }

        }
    }



    private fun setupRecyclerView(){

        newsAdapter = NewsAdapter()  //no list is passed
        binding.rvSearchNews.apply {
            layoutManager= LinearLayoutManager(activity)
            adapter=newsAdapter
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }

//        if(newsAdapter.differ.currentList.isNullOrEmpty()){
//            binding.tvSearchToFindMessage.visibility=View.VISIBLE
//            binding.ivSearchToFindImage.visibility=View.VISIBLE
//        }
    }
    

}