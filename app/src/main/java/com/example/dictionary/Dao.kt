package com.example.dictionary

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


//описание функций базы данных
@Dao
interface Dao {
    @Update
    suspend fun update(resultOfTranslate: SaveWord)
    @Delete
    suspend fun delete(resultOfTranslate: SaveWord)
    @Insert
    suspend fun insert(resultOfTranslate: SaveWord)

    @Query("SELECT * FROM save_word_table WHERE id = :id")
    fun get(id: Long): LiveData<SaveWord>
    @Query("SELECT * FROM save_word_table")
    suspend fun getAll(): List<SaveWord>
}