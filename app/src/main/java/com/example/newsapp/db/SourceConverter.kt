package com.example.newsapp.db

import androidx.room.TypeConverter
import com.example.newsapp.models.Source

object  SourceConverter {

    @TypeConverter
    fun sourceToString(source: Source): String{
        return source.name   //we're only interested in the source name and not id.
    }

    @TypeConverter
    fun stringToSource(name: String): Source {
        return Source(123,name) //putting a dummy value for id
    }

}