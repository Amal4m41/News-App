package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentBreakingNewsBinding
import com.example.newsapp.databinding.FragmentSavedNewsBinding
import com.example.newsapp.models.Article
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
import com.example.newsapp.util.Resource
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

    private var _binding: FragmentSavedNewsBinding?=null
    private val binding: FragmentSavedNewsBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    val TAG = "SavedNewsFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedNewsBinding.bind(view)

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
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment,bundle)

        })


        //get the instance value of viewmodel from the newsactivity
        viewModel = (activity as NewsActivity).viewModel

        //subscribing to all the changes w.r.t the livedata
        viewModel.getSavedNewsArticle().observe(viewLifecycleOwner, {
//            Toast.makeText(activity, "DATA CHANGED", Toast.LENGTH_SHORT).show()
            //whenever the data in the database changes, this code  block will be executed.
            newsAdapter.differ.submitList(it) //differ will compute the difference in the existing list in recycler and the passed list and then
            //only update those changes that have been changed.
        })

        setupSwipeFeatureToDeleteArticle()
    }

    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private fun hideProgressBar(){
//        binding.paginationProgressBarBreakingNews.visibility = View.INVISIBLE
//    }
//
//    private fun showProgressBar(){
//        binding.paginationProgressBarBreakingNews.visibility = View.VISIBLE
//    }


    private fun setupRecyclerView(){
        newsAdapter = NewsAdapter()  //no list is passed
        binding.rvSavedNews.apply {
            layoutManager= LinearLayoutManager(activity)
            adapter=newsAdapter
        }
    }

    private fun setupSwipeFeatureToDeleteArticle(){
        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.ACTION_STATE_IDLE,  //we don't require drag feature.
                ItemTouchHelper.RIGHT or  ItemTouchHelper.LEFT){

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false //we don't require this feature
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition //Returns the Adapter position of the item represented by this ViewHolder.
                val article = newsAdapter.differ.currentList[position]  //get the swiped article

                //Delete the article
                viewModel.deleteArticle(article)
                Log.e("ARTICLE_DELETE", "Saved news fragment: ${article.id}")

                //Adding the undo feature
                Snackbar.make(binding.root,"Successfully deleted article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.upsert(article)  //insert/save the article we just deleted.
                    }

                    show()
                }

            }
        }

        //Attach the itemtouchhelper instance with the callback to the recyclerview
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvSavedNews)
    }
}