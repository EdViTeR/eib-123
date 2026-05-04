package com.example.myapplication2 // Убедись, что тут твое название пакета!

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Все книпкусы и поля
        val gorodInput = findViewById<EditText>(R.id.gorod)
        val gorodBtn = findViewById<Button>(R.id.gorodBtn)
        val resultText = findViewById<TextView>(R.id.result)

        // Апишка
        val apiKey = "626d8c404569edb01b7ca9b85065930a"

        // Слушатель
        gorodBtn.setOnClickListener {
            val city = gorodInput.text.toString()

            if (city.isNotEmpty()) {
                resultText.text = "Узнаем погоду..." // Ждём ответа

                // Поток для интернета
                thread {
                    try {
                        // Ссылка боссенко
                        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=ru"

                        // Скачиваем джсонку
                        val apiResponse = URL(url).readText()

                        // Парсим
                        val jsonObject = JSONObject(apiResponse)

                        // Достаем температуру (из блока "main")
                        val main = jsonObject.getJSONObject("main")
                        val temp = main.getDouble("temp")

                        // Достаем описание погоды (из массива "weather")
                        val weatherArray = jsonObject.getJSONArray("weather")
                        val description = weatherArray.getJSONObject(0).getString("description")

                        // Возвращаемся в главный поток инфу
                        runOnUiThread {
                            resultText.text = "Погода: $description\nТемпература: $temp °C"
                        }

                    } catch (e: Exception) {
                        // Цепляем ошибку
                        runOnUiThread {
                            resultText.text = "Город не найден или ошибка сети"
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Введите город!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}