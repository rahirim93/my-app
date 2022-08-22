package com.example.myapp

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun log (string: String) {
    Log.d("rahirim", string)
}
// Функция получения количества рабочих дней до конца месяца (дни кроме выходных)
fun getWorkDays() : Int {
    // Сегодняшний день
    val todayDays = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    // Дней в текущем месяце
    val todayDays2 = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
    // Дней до конца месяца + 1 (+1 значит включая сегодняшний)
    val todayDays3 = todayDays2 - todayDays + 1

    var counter = 0
    var calendarForCycle = Calendar.getInstance()
    //

    repeat(todayDays3) {
        var a = calendarForCycle.get(Calendar.DAY_OF_WEEK)
        if (a == 2 || a == 3 || a == 4 || a == 5 || a == 6) {
            counter += 1
        }
        calendarForCycle.roll(Calendar.DAY_OF_MONTH, 1)
    }
    return counter
}

// Форматирование календаря в формате год.месяц "2019.09"
fun formateCalendar(calendar: Calendar): String {
    val dateFormat = SimpleDateFormat("yyyy.MM") //Настройка формата вывода в кнопку
    return dateFormat.format(calendar.time)
}

// Cтоимость часа в данном месяце
fun getPriceHour(workDays: Int, salaryMonth: Int): Int {
    return salaryMonth / (workDays * 8)
}

// Среднедневной заработок для расчета отпускных
fun averagePriceOfHolidays() {

}