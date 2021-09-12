package com.example.newsapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.models.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)  //to update/replace a record if the record id already exist in the db.
    suspend fun upsertArticle(article: Article): Long //long is the id of the inserted record

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("SELECT COUNT(*) FROM articles where url = :articleUrl")
    fun isArticleAlreadySaved(articleUrl:String):LiveData<Long>
}