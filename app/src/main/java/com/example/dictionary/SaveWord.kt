package com.example.dictionary

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//структура базы данных
@Entity("save_word_table")
data class SaveWord(
    @PrimaryKey(true)
    var id: Long = 0L,
    @ColumnInfo("currentText")
    var currentText: String = "",
    @ColumnInfo("translatedText")
    var translateText: String = "",
    @ColumnInfo("currentTextLanguage")
    var currentTextLanguage: String = "",
    @ColumnInfo("translatedTextLanguage")
    var translateTextLanguage: String = ""
)
