package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.databinding.ItemSummaryBinding
import com.example.newsapp.models.Article
import com.example.newsapp.models.Summary

class SummaryListAdapter:RecyclerView.Adapter<SummaryListAdapter.SummaryViewHolder>() {

    //creating an class property that's an instance implementing the callback definition from the abstract class.
    private val differCallback = object: DiffUtil.ItemCallback<Summary>(){
        override fun areItemsTheSame(oldItem: Summary, newItem: Summary): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Summary, newItem: Summary): Boolean {
            return oldItem == newItem
        }

    }

    //Takes the two lists and compares them to calculate the differences.(It's an async task)
    val differ = AsyncListDiffer(this,differCallback) //takes the adapter and callback as parameters.



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryListAdapter.SummaryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSummaryBinding.inflate(layoutInflater, parent, false)

        return SummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SummaryListAdapter.SummaryViewHolder, position: Int) {
        val itemSummary = differ.currentList[position]
        holder.positionValue=position  //while binding the data to this view, the position value is updated.

        holder.binding.apply {
            tvSummaryTitle.text = itemSummary.title
            tvSummarySnippet.text = itemSummary.summary
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //creating a variable of a function(lambda function)
    private var onItemClickListener:((Summary) -> Unit)?=null

    fun setOnclickListener(action: (Summary)->Unit):Unit{
        onItemClickListener=action   //attaching the onclicklistener which is a function
    }

    inner class SummaryViewHolder(val binding: ItemSummaryBinding):RecyclerView.ViewHolder(binding.root){

        var positionValue: Int = -1  //to get the clicked item index from the recycler view

        init {
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(differ.currentList[positionValue]) } //if the onItemClickListener is not null then call the function
            }
        }
    }

}


