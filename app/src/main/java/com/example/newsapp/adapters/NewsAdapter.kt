package com.example.newsapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.models.Article

/*
Implementing adapters with DiffUtil:
Normally we pass the list to the Adapter class and then whenever we want to update the list of an adapter we do something like, adapter.items = newlist and then call
adapter.notifyDatasetChanged(), but this is really inefficient as when we call notifyDatasetChanged() the recycler view
will always update all it's items even the items that didn't change.
To solve this problem we use DiffUtil as it calculates the difference between two lists and enables us to only update those items
that is different and another advantage is that this happens in the background without blocking our UI/Main thread.

 */
class NewsAdapter :
        RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

//    private var onClickListenerRecyclerListItem:OnClickListenerRecyclerListItem?=null
//

    //creating an instance implementing the callback definition
    private val differCallback = object: DiffUtil.ItemCallback<Article>(){

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
//            return oldItem.id == newItem.id //usually we can do this but in our case id is just
            // for the local db and is not set when we get the data from api

            return oldItem.url == newItem.url //two articles won't have the same url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    //Takes the two lists and compares them to calculate the differences.(It's an async task)
    val differ = AsyncListDiffer(this,differCallback) //takes the adapter and callback as parameters.


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemArticlePreviewBinding.inflate(layoutInflater, parent, false)

        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val itemArticle = differ.currentList[position]
        holder.positionValue=position  //while binding the data to this view, the position value is updated.

        holder.binding.apply {
            Glide.with(holder.binding.root).load(itemArticle.urlToImage).into(ivArticleImage)
            tvSource.text = itemArticle.source?.name
            tvDescription.text = itemArticle.description
            tvTitle.text = itemArticle.title
            tvPublishedAt.text = itemArticle.publishedAt
//            root.setOnClickListener {
//                onItemClickListener?.let { it(itemArticle) } //if the onItemClickListener is not null then call the function
//            }
        }

    }

    //creating a variable of a function(lambda function)
    private var onItemClickListener:((Article) -> Unit)?=null

    fun setOnclickListener(action: (Article)->Unit):Unit{
        onItemClickListener=action   //attaching the onclicklistener which is a function
    }

    override fun getItemCount(): Int{
        //since we don't pass the list to the adapter and instead we use list differ to manage the list, we get the
        //item count from the differ
        return differ.currentList.size
    }

//
//    fun attachOnClickListenerInterfaceInstance(interfaceInstance:OnClickListenerRecyclerListItem){
//        this.onClickListenerRecyclerListItem=interfaceInstance
//    }
//
//    interface OnClickListenerRecyclerListItem{
//        fun onClickCustom(position:Int, userObj:User, action:String)
//    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding)
        : RecyclerView.ViewHolder(binding.root) {

        var positionValue: Int = -1  //to get the clicked item index from the recycler view

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(differ.currentList[positionValue]) } //if the onItemClickListener is not null then call the function
            }
        }

    }

}