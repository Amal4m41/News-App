package com.example.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "summaries"
)
data class Summary (
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    var title:String="",
    var summary:String=""
)
