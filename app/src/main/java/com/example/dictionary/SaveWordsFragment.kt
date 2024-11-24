package com.example.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.setContent
import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder

class SaveWordsFragment : Fragment() {
    private lateinit var saveWordViewModel: SaveWordViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveWordViewModel = ViewModelProvider(this).get(SaveWordViewModel::class.java)

        return ComposeView(requireContext()).apply {
            setContent {
                FragmentScreen(saveWordViewModel, findNavController())
            }
        }

    }
}
//функция для отображения экрана со списком сохранённых слов
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun FragmentScreen(saveWordViewModel: SaveWordViewModel, navController: NavController) {
    var isNavigatingBack by remember { mutableStateOf(false) }
    // Загружаем сохранённые слова при запуске экрана
    LaunchedEffect(Unit) {
        saveWordViewModel.getAllSavedWords()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color(0xFF8B8177),
                title = { Text(text = "Словарь(сохраняйте сюда слова)", fontSize = 13.sp)  },
                // Добавляем кнопку для возврата на предыдущий экран
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!isNavigatingBack) {
                                isNavigatingBack = true
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        // Основное содержимое экрана
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF3D3029)),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Используем reversed() для инвертирования списка
                    items(saveWordViewModel.allSavedWords.reversed()) { savedWord ->
                        SavedWordItem(savedWord)
                    }
                }
            }
        }
    )
}
//функция для отображения отдельного элемента списка с сохранённым словом
@Composable
fun SavedWordItem(savedWord: SaveWord) {
    // Card используется для отображения элементов с отступами и стилизацией
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Внутри карточки отображаем два текста (текущий и переведённый текст)
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Текущий текст(${savedWord.currentTextLanguage}): ${savedWord.currentText}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Переведённый текст(${savedWord.translateTextLanguage}): ${savedWord.translateText}")
        }
    }
}

