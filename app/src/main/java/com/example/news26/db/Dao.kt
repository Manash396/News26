package com.example.news26.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RoomArticle): Long

    @Delete
    suspend fun delete(item: RoomArticle)

    @Query("SELECT * FROM articles")
    fun getAllfav() : LiveData<List<RoomArticle>>

}