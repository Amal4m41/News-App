package com.example.newsapp.ui.fragments

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
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
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.ImageViewCompat
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentTextSummarizerBinding
import com.example.newsapp.models.Article
import com.example.newsapp.models.Summary
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import com.ml.quaterion.text2summary.Text2Summary

class TextSummarizerFragment : Fragment(R.layout.fragment_text_summarizer) {


    private var _binding: FragmentTextSummarizerBinding?=null
    private val binding: FragmentTextSummarizerBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTextSummarizerBinding.bind(view)
        (activity as NewsActivity).setToolbarTitle(subtitle = "Text Summarizer")

        viewModel = (activity as NewsActivity).viewModel

        binding.apply {

            btnSummarize.setOnClickListener {
                getTextSummary()
            }

            btnSavetoClipBoard.setOnClickListener {
                copyTextToClipboard()
            }

            btnSaveSummary.setOnClickListener {
                //This button is only visible if the text is summarized successfully
                showCustomDialogForTitle()
            }

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

        if(text.isNotBlank()){
            Text2Summary.summarizeAsync( text , 0.7f , callback  )
        }
        else{
            Toast.makeText(activity, "Please provide text to summarize!", Toast.LENGTH_SHORT).show()
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

    private fun copyTextToClipboard() {
        val textToCopy = binding.tvSummarizedText.text
        val clipboardManager = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(activity, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    /**
     * Method is used to show the Custom Dialog.
     */
    private fun showCustomDialogForTitle() {
        val customDialog = activity?.let { Dialog(it) }
        /*Set the screen content from a layout resource.
    The resource will be inflated, adding all top-level views to the screen.*/


        customDialog!!.setContentView(R.layout.dialog_summary_title_input)

        val et = customDialog.findViewById<EditText>(R.id.etSummaryTitleInput)
        var summaryTitle = ""

        customDialog.findViewById<TextView>(R.id.tvSave).setOnClickListener(View.OnClickListener {
            summaryTitle = et.text.toString()
            if (summaryTitle.isNotBlank()) {
                viewModel.upsertSummary(Summary(
                        title = summaryTitle,
                        summary = binding.tvSummarizedText.text.toString()))
                Snackbar.make(binding.root,"Summary saved successfully",Snackbar.LENGTH_SHORT).show()
                customDialog.dismiss() // Dialog will be dismissed
            } else {
                Toast.makeText(activity, "Please provide a title for the summary", Toast.LENGTH_SHORT).show()
            }
        })

        customDialog.findViewById<TextView>(R.id.tvCancel).setOnClickListener(View.OnClickListener {
            customDialog.dismiss()
        })

        customDialog.setCancelable(false)
        //Start the dialog and display it on screen.
        customDialog.show()

    }

}
