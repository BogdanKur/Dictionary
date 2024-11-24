package com.example.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Фабрика для создания ViewModel (SaveWordViewModel)
class SaveWordViewModelFactory(val dao: Dao) : ViewModelProvider.Factory {

    // Переопределённая функция create() для создания экземпляра SaveWordViewModel.
    // Она вызывается, когда требуется создать ViewModel, например, в активити или фрагменте.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, что запрашиваемая ViewModel соответствует SaveWordViewModel
        if (modelClass.isAssignableFrom(SaveWordViewModel::class.java)) {
            // Если тип совпадает, создаём новый экземпляр SaveWordViewModel и передаем dao
            return SaveWordViewModel(dao) as T
        }
        // Если тип не совпадает, выбрасываем исключение
        throw IllegalArgumentException("unknown viewModel")
    }
}
