package com.example.newsapp.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentSummaryBinding
import com.example.newsapp.models.Article
import com.example.newsapp.models.Summary
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
import com.google.android.material.snackbar.Snackbar

class SummaryFragment: Fragment(R.layout.fragment_summary) {

    private var _binding: FragmentSummaryBinding?=null
    private val binding: FragmentSummaryBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel

    private val args: SummaryFragmentArgs by navArgs() // ArticleFragmentArgs is a class that was generated when we rebuild the project.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSummaryBinding.bind(view)
        (activity as NewsActivity).setToolbarTitle(subtitle = "Summary")

        viewModel = (activity as NewsActivity).viewModel

        var summary = args.summary //get the article

//        Log.e("SUMMARY", "onViewCreated: $summary")
//        setUpdateSummaryListener(summary)

        viewModel.getSummary(summary.id!!).observe(viewLifecycleOwner, Observer {
            summary=it
            setupView(summary)
        })


        binding.fabEditSummary.setOnClickListener {
            val bundle = Bundle()
            bundle.apply {
                putSerializable(Constants.SUMMARY,summary)
            }

            //we need to pass the id of the action that makes the transition from this fragment to the article fragment.
            //and the arguments that is to be passed to the article fragment.
            findNavController().navigate(R.id.action_summaryFragment_to_summaryEditFragment,bundle)
        }
    }

    private fun setupView(summary: Summary){
        binding.apply {
            tvSummaryTitle.text = summary.title
            tvSummaryContent.text = summary.summary
            tvSummaryContent.makeScrollableInsideScrollView()
        }

    }

    fun TextView.makeScrollableInsideScrollView() {
        movementMethod = ScrollingMovementMethod()
        setOnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    //It is required to call performClick() in onTouch event.
                    performClick()
                }
            }
            false
        }
    }

    //To save the article to the local db upon clicking the fab button
//    private fun setUpdateSummaryListener(summary: Summary){
//        binding.fabUpdateSummary.setOnClickListener {
//            val title = binding.tvSummaryTitle.text
//            val summaryContent = binding.summ.text
//            if(!isArticleAlreadySaved){ //if article is not saved already then save it.
//                viewModel.upsert(article)
//                Snackbar.make(binding.root,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
//            }
//            else{
//
//                viewModel.deleteArticle(article)
//                Log.e("ARTICLE_DELETE", "article fragment: ${article.id}")
//
////                Snackbar.make(binding.root,"Saved already!",Snackbar.LENGTH_SHORT).show()
//
//                //Adding the undo feature
//                Snackbar.make(binding.root,"Removed from saved",Snackbar.LENGTH_LONG).apply {
//                    setAction("Undo"){
//                        viewModel.upsert(article)  //insert/save the article we just deleted.
//                    }
//
//                    show()
//                }
//            }
//        }
//    }



    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        Toast.makeText(activity, "ONCREATE", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Toast.makeText(activity, "ONDESTROY", Toast.LENGTH_SHORT).show()
//    }

}