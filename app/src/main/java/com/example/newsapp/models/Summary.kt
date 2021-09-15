package com.example.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "summaries"
)
data class Summary (
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    var title:String="",
    var summary:String=""
): Serializable   //making it serializable so that we can transfer them between fragments.
