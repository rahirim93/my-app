package com.example.myapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.*

class StepsActivity : AppCompatActivity(), TextView.OnEditorActionListener {
    private lateinit var editTextStepsDone: EditText
    private lateinit var editTextStepsDoneToday: EditText

    private lateinit var textViewSteps: TextView
    private lateinit var textViewStepsDelay: TextView
    private lateinit var textViewStepsCatch: TextView
    private lateinit var textViewStepsCatch2: TextView


    private lateinit var editText: EditText
    private lateinit var textViewTotalSteps: TextView
    private lateinit var textViewRestSteps: TextView
    private lateinit var textViewOneSideSteps: TextView
    private lateinit var textViewGoalSteps: TextView
    private lateinit var textViewTimeOfWalking: TextView
    private lateinit var textViewSpeedOfWalking: TextView

    private lateinit var button: Button

    private var sharedPreferences: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_steps)

        init()

        loadSharedPreferences()

        countSteps()

        //test()
    }

    private fun init() {
        editTextStepsDone = findViewById(R.id.editTextStepsDone)
        editTextStepsDone.setOnEditorActionListener(this)
        editTextStepsDoneToday = findViewById(R.id.editTextStepsDoneToday)
        editTextStepsDoneToday.setOnEditorActionListener(this)

        textViewSteps = findViewById(R.id.textViewSteps)
        textViewStepsDelay = findViewById(R.id.textViewStepsDelay)
        textViewStepsCatch= findViewById(R.id.textViewStepsCatch)
        textViewStepsCatch2 = findViewById(R.id.textViewStepsCatch2)

        editText = findViewById(R.id.editTextNumber)
        textViewTotalSteps = findViewById(R.id.textViewTotalSteps)
        textViewRestSteps = findViewById(R.id.textViewRestSteps)
        textViewOneSideSteps = findViewById(R.id.textViewOneSideSteps)
        textViewGoalSteps = findViewById(R.id.textViewTest)
        textViewTimeOfWalking = findViewById(R.id.textViewTimeOfWalking)
        textViewSpeedOfWalking = findViewById(R.id.textViewSpeedOfWalking)
        button = findViewById(R.id.button)

        sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            countSteps()
        }
        return false
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

    private fun countSteps() {
        // Сегодняшний день
        val todayDays = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        // Количество дней в текущем месяце
        val daysInCurrentMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        // Дней осталось до конца месяца, включая сегодняшний (+1)
        val daysTillEndMonth = daysInCurrentMonth - todayDays + 1

        var stepsDone = 0
        var stepsDoneToday = 0

        if ((editTextStepsDone.text).toString() != "") {
            stepsDone = (editTextStepsDone.text).toString().toInt()
        }
        if ((editTextStepsDoneToday.text).toString() != "") {
            stepsDoneToday = (editTextStepsDoneToday.text).toString().toInt()
        }

        // Шагов должно быть сделано на сегодня
        val stepsMustDone = 20000 * todayDays
        // Отставание по шагам
        val stepsDelay = stepsMustDone - (stepsDone - stepsDoneToday)
        // Шагов в день чтобы догнать норму в месяц во все дни
        val stepsCatchAllDays = stepsDelay / daysTillEndMonth + 20000
        // Шагов в день чтобы догнать норму в только рабочие дни
        val stepsCatchWorksDays = stepsDelay / getWorkDays() + 20000

        textViewSteps.text = "Шагов должно быть сделано на сегодня: $stepsMustDone"

        textViewStepsDelay.text = "Отставание по шагам: $stepsDelay"

        textViewStepsCatch.text = "Шагов во все дни чтобы догнать: $stepsCatchAllDays"

        textViewStepsCatch2.text = "Шагов в рабочие дни чтобы догнать: $stepsCatchWorksDays \n\n" +
                "Дней до конца месяца: $daysTillEndMonth \n\n" +
                "Рабочих дней до конца месяца: ${getWorkDays()}"
    }

    private fun savePreferences(stepsDone: Int, stepsDoneToday: Int) {
        val editor = sharedPreferences?.edit()
        editor?.putInt("stepsDone", stepsDone)
        editor?.putInt("stepsDoneToday", stepsDoneToday)
        editor?.apply()
    }

    override fun onStop() {
        super.onStop()
        savePreferences(
            (editTextStepsDone.text).toString().toInt(),
            (editTextStepsDoneToday.text).toString().toInt()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        savePreferences(
            (editTextStepsDone.text).toString().toInt(),
            (editTextStepsDoneToday.text).toString().toInt()
        )
    }

    private fun loadSharedPreferences() {
        editTextStepsDone.setText(sharedPreferences?.getInt("stepsDone", 0).toString())
        editTextStepsDoneToday.setText(sharedPreferences?.getInt("stepsDoneToday", 0).toString())
    }
}