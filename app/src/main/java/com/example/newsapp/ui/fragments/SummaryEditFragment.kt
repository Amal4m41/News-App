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
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSummaryEditBinding
import com.example.newsapp.models.Article
import com.example.newsapp.models.Summary
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SummaryEditFragment: Fragment(R.layout.fragment_summary_edit) {

    private var _binding: FragmentSummaryEditBinding?=null
    private val binding: FragmentSummaryEditBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel

    private val args: SummaryEditFragmentArgs by navArgs() // ArticleFragmentArgs is a class that was generated when we rebuild the project.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSummaryEditBinding.bind(view)
        (activity as NewsActivity).setToolbarTitle(subtitle = "Edit Summary")

        viewModel = (activity as NewsActivity).viewModel

        val summary = args.summary //get the article


        setupEditView(summary)
        setEditSummaryListener(summary)
    }

    private fun setupEditView(summary: Summary){
        binding.apply {
            etSummaryTitle.setText(summary.title)
            etSummaryContent.setText(summary.summary)
        }
    }



    //To update the summary to the local db upon clicking the fab button
    private fun setEditSummaryListener(summary: Summary){
        binding.fabUpdateSummary.setOnClickListener {
            val title = binding.etSummaryTitle.text.toString()
            val summaryContent = binding.etSummaryContent.text.toString()
            var isBlank=false

            if(title.isBlank())
            {
                binding.summaryTitleTil.error="Title cannot be empty"
                isBlank=true
            }
            else {
                binding.summaryTitleTil.isErrorEnabled = false
            }

            if(summaryContent.isBlank())
            {
                binding.summaryContentTil.error="Summary cannot be empty"
                isBlank=true
            }
            else {
                binding.summaryContentTil.isErrorEnabled = false
            }


            if(isBlank){
                return@setOnClickListener
            }
            else if(summary.title == title && summary.summary == summaryContent){ //if article is not saved already then save it.
                Toast.makeText(activity, "No changes to update", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()  //get back to the previous fragment(w.r.t fragment stack)
            }
            else{
                viewModel.upsertSummary(Summary(id=summary.id,title=title,summary = summaryContent))
                Snackbar.make(binding.root,"Updated summary",Snackbar.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
        }
    }



    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}