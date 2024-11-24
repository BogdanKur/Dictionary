package com.example.dictionary

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder

var translated: String = "" // Переменная для хранения переведенного текста
// Функция для перевода текста с использованием Google Translate
fun translateText(text: String, targetLanguage: String) {
    val client = OkHttpClient()

    val urlEncodedText = URLEncoder.encode(text, "UTF-8")
    val requestUrl = "https://translate.google.com/m?hl=$targetLanguage&q=$urlEncodedText"
    val request = Request.Builder()
        .url(requestUrl)
        .build()

    client.newCall(request).enqueue(object: Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        @SuppressLint("ResourceAsColor")
        override fun onResponse(call: Call, response: Response) {
            if(response.isSuccessful) {
                val responseData = response.body?.string()
                translated = extractTranslation(responseData)
            }
        }

    })
}
// Функция для извлечения перевода из HTML-страницы
fun extractTranslation(html: String?): String {
    val doc = Jsoup.parse(html)
    val translatedText = doc.select("div.result-container").text()
    return translatedText
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this).get(TranslationViewModel::class.java)
        val dao = SaveWordDatabase.getInstance(applicationContext).dao
        val viewModelFactory = SaveWordViewModelFactory(dao)
        val saveWordViewModel = ViewModelProvider(this, viewModelFactory)[SaveWordViewModel::class.java]
        setContent {
            NavigationGraph(viewModel, applicationContext, saveWordViewModel) // Настройка графа навигации для экрана
        }
    }
}
// Функция для навигации между экранами
@Composable
fun NavigationGraph(viewModel: TranslationViewModel, context: Context, saveWordViewModel: SaveWordViewModel) {
    val navController = rememberNavController()

    // Флаг состояния навигации
    var isNavigating by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            TranslationScreen(navController, viewModel, context, onNavigate = {
                if (!isNavigating) {
                    isNavigating = true
                    navController.navigate("fragment")
                }
            })
        }
        composable("fragment") {
            isNavigating = false
            FragmentScreen(saveWordViewModel, navController)
        }
    }
}



@Composable
fun TranslationScreen(navController: NavController, viewModel: TranslationViewModel, context: Context, onNavigate: () -> Unit) {
    val currentLanguage = remember { mutableStateOf("Казахский") }
    val targetLanguage = remember { mutableStateOf("Русский") }
    val abbrCode = remember { mutableStateOf("ru") }
    val textState = remember { mutableStateOf("") }
    val translateTextState = remember { mutableStateOf("Перевод") }
    val job = remember { mutableStateOf<Job?>(null) }
    // Запуск перевода каждый раз, когда текст меняется
    LaunchedEffect(textState.value) {
        translateText(textState.value, abbrCode.value)
    }
    // Запуск задачи по обновлению перевода в фоновом потоке
    LaunchedEffect(Unit) {
        job.value = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                if (textState.value.isNotEmpty() && translated.isNotEmpty()) {
                    translateTextState.value = translated
                } else {
                    translateTextState.value = "Перевод"
                }
                delay(200)
            }
        }
    }
    // Основной экран
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3D3029))
    ) {
        // Верхняя панель с кнопками очистки и словарём
        TopAppBar(
            backgroundColor = Color(0xFF8B8177),
            title = {},
            actions = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Кнопка для очистки текста
                    IconButton(
                        onClick = {
                            textState.value = ""
                            translateTextState.value = "Перевод"
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_clear_text),
                            contentDescription = "Clear",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    // Кнопка для перехода в словарь
                    IconButton(
                        onClick = {
                            textState.value = ""
                            translateTextState.value = "Перевод"
                            translated = ""
                            abbrCode.value = "ru"
                            onNavigate()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_history_of_translate),
                            contentDescription = "History",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        )

        // Панель выбора языков
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFBEBAB0)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentLanguage.value,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            )
            // Кнопка для смены языков
            IconButton(onClick = {
                if(abbrCode.value == "ru") {
                    abbrCode.value = "kk"
                    currentLanguage.value = "Русский"
                    targetLanguage.value = "Казахский"
                    if(textState.value != "") {
                        val tmp = textState.value
                        textState.value = translateTextState.value
                        translateTextState.value = tmp
                        translateText(textState.value, abbrCode.value)
                    }
                } else {
                    abbrCode.value = "ru"
                    currentLanguage.value = "Казахский"
                    targetLanguage.value = "Русский"
                    if(textState.value != "") {
                        val tmp = textState.value
                        textState.value = translateTextState.value
                        translateTextState.value = tmp
                        translateText(textState.value, abbrCode.value)
                    }
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.exchange_language),
                    contentDescription = "Swap Languages",
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = targetLanguage.value,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            )
        }

        // Поля для ввода текста и отображения перевода
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF8D8D8D))
                    .padding(16.dp)
            ) {
                if (textState.value.isEmpty()) {
                    Text(
                        text = "Введите текст",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                BasicTextField(
                    value = textState.value,

                    onValueChange = { newText ->
                        textState.value = newText
                    },
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
                )
            }
            // Блок для отображения перевода
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF303030))
                    .padding(16.dp)
            ) {
                Text(
                    text = translateTextState.value,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        // Кнопка для сохранения перевода в словарь
        Button(
            onClick = {
                if(textState.value != "" && translateTextState.value != "") {
                    val dao = SaveWordDatabase.getInstance(context).dao
                    viewModel.saveWord(textState.value, translateTextState.value, currentLanguage.value, targetLanguage.value, dao)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFBEBAB0)
                    )
        ) {
            Text("Сохранить перевод в словарь")
        }
    }
}



