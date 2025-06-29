package com.example.news26.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [RoomArticle::class], version = 2
)
@TypeConverters(Convertors::class)
abstract class ArticleDatabase : RoomDatabase() {

    // will be implemented by room and it return an Dao object containing all the implemented method
    abstract fun getDao() : Dao

    companion object{
        @Volatile
        private var INSTANCE  : ArticleDatabase? =null
        private val LOCK = Any()

        operator fun invoke(context: android.content.Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: createDatabase(context).also{
                INSTANCE = it
            }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article.db"
            ).fallbackToDestructiveMigration(true)
                .build()

    }
}