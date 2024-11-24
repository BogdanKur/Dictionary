package com.example.dictionary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SaveWord::class], version = 1, exportSchema = false)
abstract class SaveWordDatabase: RoomDatabase() {
    abstract val dao: Dao

    companion object{
        @Volatile
        private var INSTANCE: SaveWordDatabase? = null
        //функция для инициализации базы данных
        fun getInstance(context: Context): SaveWordDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SaveWordDatabase::class.java,
                        "save_word_table"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}