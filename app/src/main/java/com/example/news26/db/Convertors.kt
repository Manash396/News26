package com.example.news26.db

import androidx.room.TypeConverter
import com.example.news26.data.Source

class Convertors {

    @TypeConverter
    fun fromSource(source: Source) : String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String): Source{
        return Source(name,name)
    }
}