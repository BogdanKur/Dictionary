package com.example.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TranslationViewModel() : ViewModel() {
    //функция сохранения слова в базу данных
    fun saveWord(currentText: String, translatedText: String, currentTextLanguage: String, translatedTextLanguage: String, saveWordDao: Dao) {
        viewModelScope.launch {
            val saveWord = SaveWord(currentText = currentText, translateText = translatedText, currentTextLanguage = currentTextLanguage, translateTextLanguage = translatedTextLanguage)
            saveWordDao.insert(saveWord)
        }
    }
}