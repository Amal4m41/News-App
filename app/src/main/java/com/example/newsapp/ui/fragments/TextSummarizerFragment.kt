package com.example.newsapp.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentTextSummarizerBinding
import com.example.newsapp.models.Article
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import com.ml.quaterion.text2summary.Text2Summary

class TextSummarizerFragment : Fragment(R.layout.fragment_text_summarizer) {


    private var _binding: FragmentTextSummarizerBinding?=null
    private val binding: FragmentTextSummarizerBinding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTextSummarizerBinding.bind(view)

        binding.btnSummarize.setOnClickListener {
            getTextSummary()
        }
    }

    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    val callback = object : Text2Summary.SummaryCallback {
        override fun onSummaryProduced(summary: String) {

            val listOfPoints = summary.split("\\.\\s*\\.".toRegex())
            var stringValue = ""
            for(line in listOfPoints){
                stringValue+="\u2022"+line+"\n\n"
            }
            binding.clSummaryResult.visibility=View.VISIBLE
            binding.tvSummarizedText.text = stringValue
//            binding.progressBar.visibility=View.INVISIBLE
//            binding.tvSummarizedText.movementMethod=ScrollingMovementMethod()
            binding.tvSummarizedText.makeScrollableInsideScrollView()
        }
    }

    private fun getTextSummary(){
//        binding.progressBar.visibility= View.VISIBLE
        val text = binding.etTextInput.text.toString()

        Text2Summary.summarizeAsync( text , 0.7f , callback  )
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

}
