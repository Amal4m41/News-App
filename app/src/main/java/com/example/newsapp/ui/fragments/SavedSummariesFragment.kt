package com.example.newsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentMoreBinding
import com.example.newsapp.databinding.FragmentSavedSummariesBinding
import com.example.newsapp.ui.NewsActivity

class SavedSummariesFragment : Fragment(R.layout.fragment_saved_summaries) {

    private var _binding: FragmentSavedSummariesBinding?=null
    private val binding: FragmentSavedSummariesBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedSummariesBinding.bind(view)

//        viewModel = (activity as NewsActivity).viewModel

//        val article = args.article //get the article

    }


    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}