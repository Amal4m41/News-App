package com.example.newsapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentMoreBinding
import com.example.newsapp.ui.NewsActivity

class MoreFragment : Fragment(R.layout.fragment_more) {

    private var _binding: FragmentMoreBinding?=null
    private val binding: FragmentMoreBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMoreBinding.bind(view)
        (activity as NewsActivity).setToolbarTitle(subtitle = "More")

//        viewModel = (activity as NewsActivity).viewModel

//        val article = args.article //get the article

        binding.apply {

            tvTextSummarizer.setOnClickListener {
                findNavController().navigate(R.id.action_moreFragment2_to_textSummarizerFragment)
            }

            tvSavedSummaries.setOnClickListener{
                findNavController().navigate(R.id.action_moreFragment2_to_savedSummariesFragment)
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