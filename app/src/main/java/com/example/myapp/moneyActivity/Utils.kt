package com.example.myapp.moneyActivity

import android.util.Log
import android.widget.EditText
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

// Дней до аванса
fun daysToPrePay(): Long {
    val today = Calendar.getInstance() // Сегодняшняя дата

    // Аванс в этом месяце
    val prePayDayThisMonth = Calendar.getInstance()
    prePayDayThisMonth.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 27, 0, 0, 0)

    // Аванс в следующем месяце
    val prePayDayNextMonth = Calendar.getInstance()
    prePayDayNextMonth.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, 27, 0, 0, 0)

    val dateFormat = SimpleDateFormat("dd.MM.yyyy") //Настройка формата вывода


    //Log.d("rahirim", dateFormat.format(prePayDay.time))

    // Дней до аванса в этом месяце
    val daysToPrePayThisMonth = Duration.between(today.toInstant(), prePayDayThisMonth.toInstant()).toDays()
    // Дней до аванса в следующем месяце
    val daysToPrePayNextMonth = Duration.between(today.toInstant(), prePayDayNextMonth.toInstant()).toDays()

    //Log.d("rahirim", daysToPrePay.toString())

    return if (today.get(Calendar.DAY_OF_MONTH) > 27) {
        daysToPrePayNextMonth
    } else daysToPrePayThisMonth
}

// Дней до зп
fun daysToPay(): Long {
    val today = Calendar.getInstance() // Сегодняшняя дата

    // Зарплата в этом месяце
    val payDayCurrentMonth = Calendar.getInstance()
    payDayCurrentMonth.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 12)
    val daysToPayCurrentMonth = Duration.between(today.toInstant(), payDayCurrentMonth.toInstant()).toDays()

    // ЗП в следующем месяце
    val payDayNextMonth = Calendar.getInstance()
    payDayNextMonth.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH) + 1, 12, 0, 0, 0)
    val daysToPayNextMonth = Duration.between(today.toInstant(), payDayNextMonth.toInstant()).toDays()

    return if (today.get(Calendar.DAY_OF_MONTH) > 12) {
        daysToPayNextMonth + 1
    } else {
        daysToPayCurrentMonth
    }
}

// Получение значения из EditText
fun getTextFromEditText(editText: EditText): Int {
    return (editText.text).toString().toInt()
}

fun log(string: String) {
    Log.d("rahirim", string)
}

// Вывод даты из календаря
fun dateFromCalendar(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy") //Настройка формата вывода даты

    return dateFormat.format(calendar.time)
}