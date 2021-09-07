package com.example.newsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey


/*
Since user can save articles for later we make the article data class an entity/table for the room database.
 */


@Entity(
    tableName = "articles"
)
data class Article(
        @PrimaryKey(autoGenerate = true) //autogenerate the primary key
    var id:Int? = null,
        val author: Any,
        val content: String,
        val description: String,
        val publishedAt: String,
        val source: Source,
        val title: String,
        val url: String,
        val urlToImage: String
)