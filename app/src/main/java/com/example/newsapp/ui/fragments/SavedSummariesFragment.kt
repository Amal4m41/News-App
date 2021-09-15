package com.example.newsapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.adapters.SummaryListAdapter
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.databinding.FragmentMoreBinding
import com.example.newsapp.databinding.FragmentSavedSummariesBinding
import com.example.newsapp.models.Summary
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Constants
import com.example.newsapp.util.UtilFuns
import com.google.android.material.snackbar.Snackbar

class SavedSummariesFragment : Fragment(R.layout.fragment_saved_summaries) {

    private var _binding: FragmentSavedSummariesBinding?=null
    private val binding: FragmentSavedSummariesBinding get() = _binding!!

    private lateinit var viewModel:NewsViewModel
    private lateinit var summaryAdapter: SummaryListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSavedSummariesBinding.bind(view)

        viewModel = (activity as NewsActivity).viewModel

        setupRecyclerView()

        //Subscribing to the livedata w.r.t summary list in db.
        viewModel.getSavedSummaries().observe(viewLifecycleOwner
                , object: Observer<List<Summary>>{
            override fun onChanged(summaryList: List<Summary>?) {
                summaryAdapter.differ.submitList(summaryList)
            }
        })

    }


    //Note: Fragments outlive their views. Make sure you clean up any references to the binding class
    // instance in the fragment's onDestroyView() method.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView(){
        summaryAdapter = SummaryListAdapter()  //no list is passed
        binding.rvSavedSummaries.apply {
            layoutManager= LinearLayoutManager(activity)
            adapter=summaryAdapter
        }

        summaryAdapter.setOnclickListener(fun(summary:Summary):Unit{
            val bundle = Bundle()
            bundle.apply {
                putSerializable(Constants.SUMMARY,summary)
            }

            //we need to pass the id of the action that makes the transition from this fragment to the article fragment.
            //and the arguments that is to be passed to the article fragment.
            findNavController().navigate(R.id.action_savedSummariesFragment_to_summaryFragment,bundle)
        })

        setupSwipeFeatureToDeleteSummary()
    }

    private fun setupSwipeFeatureToDeleteSummary(){
        val itemTouchHelperCallback = UtilFuns.getOnItemSwipeCallback(object: UtilFuns.ItemSwipeCallbackInterface{
            override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder) {
                val position = viewHolder.adapterPosition //Returns the Adapter position of the item represented by this ViewHolder.
                val summary = summaryAdapter.differ.currentList[position]  //get the swiped article

                //Delete the article
                viewModel.deleteSummary(summary)
                Log.e("SUMMARY_DELETE", "Saved summaries fragment: ${summary.id}")

                //Adding the undo feature
                Snackbar.make(binding.root,"Successfully deleted summary", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.upsertSummary(summary)  //insert/save the article we just deleted.
                    }

                    show()
                }
            }

        })

        //Attach the itemtouchhelper instance with the callback to the recyclerview
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.rvSavedSummaries)
    }

}