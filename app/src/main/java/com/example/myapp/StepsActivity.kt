package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class StepsActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var textViewTotalSteps: TextView
    private lateinit var textViewRestSteps: TextView
    private lateinit var textViewOneSideSteps: TextView
    private lateinit var textViewGoalSteps: TextView
    private lateinit var textViewTimeOfWalking: TextView
    private lateinit var textViewSpeedOfWalking: TextView

    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        init()
    }

    private fun init() {
        editText = findViewById(R.id.editTextNumber)
        textViewTotalSteps = findViewById(R.id.textViewTotalSteps)
        textViewRestSteps = findViewById(R.id.textViewRestSteps)
        textViewOneSideSteps = findViewById(R.id.textViewOneSideSteps)
        textViewGoalSteps = findViewById(R.id.textViewGoalSteps)
        textViewTimeOfWalking = findViewById(R.id.textViewTimeOfWalking)
        textViewSpeedOfWalking = findViewById(R.id.textViewSpeedOfWalking)
        button = findViewById(R.id.button)
    }

    fun count(view: android.view.View) {
        if (editText.text.isNotEmpty()) {
            val speedOfWalking: Int = 115 //Примерная скорость ходьбы шагов в минуту
            val goal: Int = 23000 // Целевое количество шагов
            val currentSteps = editText.text.toString().toInt() //Количество шагов считанное с editText
            val restSteps = goal - currentSteps // Оставшееся количество шагов до целевого
            val stepsOneSide = restSteps / 2 // Количество шагов в одну сторону для выполнения целевого
            val goalSteps = currentSteps + stepsOneSide // Количество шагов до когорого надо дойти сейчас чтобы выполнить цель
            val minutesOfWalking = (restSteps / speedOfWalking) // Время прогулки в минутах
            val hoursOfWalking = minutesOfWalking / 60 // Количество целых часов
            val restMinutesOfWalking = minutesOfWalking % 60 // Количество минут за вычетом целых часов


            textViewTotalSteps.text = "Цель: $goal"
            textViewRestSteps.text = "Осталось: $restSteps"
            textViewOneSideSteps.text = "В одну сторону: $stepsOneSide"
            textViewGoalSteps.text = "Идти до: $goalSteps"
            textViewSpeedOfWalking.text = "Примерная скорость ходьбы: $speedOfWalking шаг./мин."
            textViewTimeOfWalking.text = "Примерное время прогулки: $hoursOfWalking ч. $restMinutesOfWalking мин."
        } else {
            Toast.makeText(this, "Введите значение", Toast.LENGTH_LONG).show()
        }


    }
}