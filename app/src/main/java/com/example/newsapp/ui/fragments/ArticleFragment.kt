package com.example.newsapp.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.models.Article
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment: Fragment(R.layout.fragment_article) {

    private var _binding: FragmentArticleBinding?=null
    private val binding: FragmentArticleBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel
    private var isArticleAlreadySaved:Boolean = false  //to show red heart in fab and to prevent multiple copies of saved articles.

    val args: ArticleFragmentArgs by navArgs() // ArticleFragmentArgs is a class that was generated when we rebuild the project.

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentArticleBinding.bind(view)

        (activity as NewsActivity).setToolbarTitle(subtitle = "Article")

        viewModel = (activity as NewsActivity).viewModel

        val article = args.article //get the article

        

        viewModel.isArticleSavedAlready(article.url).observe(viewLifecycleOwner, {

            if(it == null){
                isArticleAlreadySaved = false
            }
            else{
                isArticleAlreadySaved = it > 0L
            }

            Log.e("Is saved", "onViewCreated: $isArticleAlreadySaved , $it")
            if (isArticleAlreadySaved) {
                //Change the color of the fab button heart to red.
                ImageViewCompat.setImageTintList(
                        binding.fabSaveArticle,
                        ColorStateList.valueOf(Color.RED)
                )

                article.id = it!!.toInt()  //get the id w.r.t the database, so that we can now delete it from the db in case.

            } else {
                //Change the color of the fab button heart to white.
                ImageViewCompat.setImageTintList(
                        binding.fabSaveArticle,
                        ColorStateList.valueOf(Color.WHITE)
                )
            }
        })



        setUpWebView(article)
        setSaveArticleListener(article)
    }

    private fun setUpWebView(article: Article){
        binding.webView.apply {
            webViewClient = WebViewClient() //makes sure that the page will open inside the webview and not in browser.
            loadUrl(article.url)
        }
    }

    //To save the article to the local db upon clicking the fab button
    private fun setSaveArticleListener(article:Article){
        binding.fabSaveArticle.setOnClickListener {
            if(!isArticleAlreadySaved){ //if article is not saved already then save it.
                viewModel.upsert(article)
                Snackbar.make(binding.root,"Article saved successfully",Snackbar.LENGTH_SHORT).show()
            }
            else{

                viewModel.deleteArticle(article)
                Log.e("ARTICLE_DELETE", "article fragment: ${article.id}")

//                Snackbar.make(binding.root,"Saved already!",Snackbar.LENGTH_SHORT).show()

                //Adding the undo feature
                Snackbar.make(binding.root,"Removed from saved",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.upsert(article)  //insert/save the article we just deleted.
                    }

                    show()
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

}