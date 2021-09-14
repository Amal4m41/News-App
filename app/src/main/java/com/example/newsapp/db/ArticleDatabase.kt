package com.example.newsapp.db

import android.content.Context
import androidx.room.*
import com.example.newsapp.models.Article
import com.example.newsapp.models.Summary

@Database(
    entities = [Article::class,Summary::class],
    version = 1
)
@TypeConverters(  //room only supports primitive data types therefore we add converters to convert source object to and from
// string while storing and retrieving the data.
        SourceConverter::class
)
abstract class ArticleDatabase:RoomDatabase() {

    //The implementation of this function and the DAO interface will be done by Room.
    abstract fun getArticleDao():ArticleDao
    abstract fun getSummaryDao():SummaryDao

    companion object{
        @Volatile  //this means that other threads can immediately see when a thread changes this instance.
        private var instance: ArticleDatabase?=null  //singleton instance

        private val LOCK=Any()

        //Will be called whenever we create an instance of this class(Making this class a singleton).
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            //what happens inside this block can't be accessed by other threads at the same time.
            instance ?: createDatabase(context).also{
                instance = it
            }
        }

        private fun createDatabase(context: Context): ArticleDatabase {

            return Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_db.db"
                ).build()
        }

    }
}