package com.example.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.models.Article
import com.example.newsapp.models.Summary

@Dao
interface SummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)  //to update/replace a record if the record id already exist in the db.
    suspend fun upsertSummary(summary: Summary): Long //long is the id of the inserted record

    @Delete
    suspend fun deleteSummary(summary: Summary)

    @Query("SELECT * FROM summaries")
    fun getAllSummaries(): LiveData<List<Summary>>

    @Query("SELECT * FROM summaries where summaries.id=:id")
    fun getSummary(id:Int): LiveData<Summary>

}