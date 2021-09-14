package com.example.newsapp.util

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.google.android.material.snackbar.Snackbar

object UtilFuns {

    fun getOnItemSwipeCallback(interfaceInstance: ItemSwipeCallbackInterface): ItemTouchHelper.SimpleCallback{

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.ACTION_STATE_IDLE,  //we don't require drag feature.
                ItemTouchHelper.RIGHT or  ItemTouchHelper.LEFT){

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false //we don't require this feature
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                interfaceInstance.onItemSwiped(viewHolder)


            }
        }

        return itemTouchHelperCallback
    }

//    /**
//     * Method is used to show the Custom Progress Dialog.
//     */
//    fun customProgressDialogFunction(context: Context) {
//        val customProgressDialog = Dialog(context)
//
//        /*Set the screen content from a layout resource.
//        The resource will be inflated, adding all top-level views to the screen.*/
//        customProgressDialog.setContentView(R.layout.dialog_custom_progress)
//
//        //Start the dialog and display it on screen.
//        customProgressDialog.show()
//        customProgressDialog.setCancelable(true)
////        customProgressDialog.dismiss()
//    }


    interface ItemSwipeCallbackInterface{
        fun onItemSwiped(viewHolder: RecyclerView.ViewHolder)
    }
}