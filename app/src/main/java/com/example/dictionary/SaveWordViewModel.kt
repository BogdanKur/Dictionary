package com.example.dictionary

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SaveWordViewModel(private val saveWordDao: Dao) : ViewModel() {
    // Список всех сохранённых слов. Используется для отображения в UI.
    val allSavedWords = mutableStateListOf<SaveWord>()

    init {
        getAllSavedWords()
    }

    // Функция для загрузки всех сохранённых слов из базы данных.
    fun getAllSavedWords() {
        viewModelScope.launch {
            allSavedWords.clear()
            allSavedWords.addAll(saveWordDao.getAll())
        }
    }
}