package com.example.myapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.myapp.moneyActivity.daysToPay
import com.example.myapp.moneyActivity.daysToPrePay
import com.example.myapp.moneyActivity.getTextFromEditText
import java.util.*

// Добавить дней до аванса и до зарплаты
// Вынести отдельную функцию для получения значения с editText
// Если editText пустой, то при сохранения в shared preferences то вылетает исключение
// Вынести в отдельную функцию сохранение состояния
// Если в editText ничего нет, то при закрытии приложения выелтает исключение, исправить
// Во всех editText обработать случай если поле пустое, т.к. вылетает исключение

/**
 * есть желаемая сумма в месяц. есть количество денег потраченных на сегодняшний день.
 * разница будет количество свободных денег.
 * разница будет различаться в завимости от того выплачена аренда или нет
 *
 *
 */

class Money2Activity : AppCompatActivity(), TextView.OnEditorActionListener {

    private lateinit var editTextSalary: EditText       // Поле для ввода оклада
    private lateinit var editTextPercentBonus: EditText // Поле для ввода процента премии

    private lateinit var editTextMoneyFact: EditText
    private lateinit var editTextMoneyWished: EditText
    private lateinit var editTextRent: EditText
    private lateinit var editTextMoneyNow: EditText
    private lateinit var editTextSalaryFact: EditText


    private lateinit var textViewMoneyADay: TextView
    private lateinit var textViewMoneyAMonth: TextView
    private lateinit var textViewSeekBarProgress: TextView
    private lateinit var textViewPref: TextView
    private lateinit var textViewMoneyAvailable : TextView
    private lateinit var textViewSalaryWithBonus: TextView  // Зарплата с премией
    private lateinit var textViewMinusTax: TextView         // Зарплата с вычетом налога
    private lateinit var textViewDaysToPay: TextView        // Дней до зарплаты
    private lateinit var textViewDaysToPrepay: TextView     // Дней до аванса

    private lateinit var checkBox: CheckBox

    private lateinit var seekBar: SeekBar

    private var pref2: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money2)

        init()

        seekBarsInit()

        //Подгрузка сохраненного состояния
        textViewPref.text = pref2?.getInt("money2", 0).toString()
        editTextMoneyFact.setText(pref2?.getInt("money2", 0).toString())
        editTextMoneyWished.setText(pref2?.getInt("moneyWished2", 0).toString())
        editTextRent.setText(pref2?.getInt("rent2", 0).toString())
        editTextMoneyNow.setText(pref2?.getInt("moneyNow2", 0).toString())
        editTextSalaryFact.setText(pref2?.getInt("salaryFact2", 0).toString())
        editTextSalary.setText(pref2?.getInt("salary2", 0).toString())
        editTextPercentBonus.setText(pref2?.getInt("percentBonus2", 0).toString())

        //Подгрузка сохраненного состояния checkBox при запуске
        val stateOfCheckBox = pref2?.getBoolean("checkBox2", false)
        if (checkBox.isChecked != stateOfCheckBox) checkBox.toggle()

        countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
    }


    private fun seekBarsInit() {
        seekBar.min = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)                 // Минимальное значение seekBar (дней с начала месяца)
        //seekBar.max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)  // Максимальное значение seekBat (дней в месяце)
        seekBar.max = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + 5           // Максимальное значение seekBat текущий день плюс 5 дней
        seekBar.progress = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)            // Прогресс seekBar (дней с начала месяца)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textViewSeekBarProgress.text = "$progress"
                countMoney(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun countMoney(todayDay: Int) {
        // Получаем арендную плату из editText
        val rent = getTextFromEditText(editTextRent)

        //Получение и приведение значения фактического потраченного количества денег в editText к типу Int
        val moneyFact = getTextFromEditText(editTextMoneyFact)

        // Количество дней в текущем месяце
        val countDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)

        //Среднее количество денег которое тратилось в день до сегодняшнего дня
        val moneyOfDay = if (checkBox.isChecked) {  // Если квартплата уже внесена
            (moneyFact - rent) / todayDay               // то вычитаем ее из суммы
        } else {
            moneyFact / todayDay                        // Если нет, то просто делим на количество дней
        }
        textViewMoneyADay.text = "Денег в день: $moneyOfDay"    // Выводим в textView

        // Количество денег в месяц с учетом квартплаты
        val moneyMonth = (moneyOfDay * countDayMonth) + rent   // Количество денег в месяц вместе с квартплатой
        textViewMoneyAMonth.text = "Денег в месяц: $moneyMonth"

        // Подсчет и вывод количества денег, которые можно потратить сегодня
        //Получение и приведение значения в поле к типу Int
        val moneyWished = getTextFromEditText(editTextMoneyWished) // Желаемая сумма в месяц
        if(moneyWished > 0) {
            val moneyAvailable = if (checkBox.isChecked) {
                ((moneyWished - rent) / countDayMonth * todayDay - moneyFact) + rent //Сколько можно потратить сегодня (вычет аренды)
            } else {
                (moneyWished - rent) / countDayMonth * todayDay - moneyFact //Сколько можно потратить сегодня
            }
            textViewMoneyAvailable.text = "Можно потратить: $moneyAvailable"
        }

        val salary = getTextFromEditText(editTextSalary)                    // Оклад
        val percentBonus = getTextFromEditText(editTextPercentBonus) + 100  // Процент премии
        val salaryWithBonus = (salary * percentBonus) / 100
        val salaryMinusTax = salaryWithBonus * 87 / 100

        textViewSalaryWithBonus.text = "Зарплата с премией: $salaryWithBonus"
        textViewMinusTax.text = "Зарплата с вычетом налога: $salaryMinusTax"

        //textViewDaysToPay.text = "Дней до зарплаты: ${daysToPay()}"
        textViewDaysToPrepay.text = "Дней до аванса: ${daysToPrePay()}"
    }

    private fun saveMoneyPref(money: Int, checkBox: Boolean, moneyWished: Int, rent: Int, moneyNow: Int, salaryFact: Int, salary: Int, percentBonus: Int) {
        val editor = pref2?.edit()
        editor?.putInt("money2", money)
        editor?.putBoolean("checkBox2", checkBox)
        editor?.putInt("moneyWished2", moneyWished)
        editor?.putInt("rent2", rent)
        editor?.putInt("moneyNow2", moneyNow)
        editor?.putInt("salaryFact2", salaryFact)
        editor?.putInt("salary2", salary)
        editor?.putInt("percentBonus2", percentBonus)
        editor?.apply()
    }

    override fun onStop() {
        super.onStop()
        saveMoneyPref(
            editTextMoneyFact.text.toString().toInt(),
            checkBox.isChecked,
            editTextMoneyWished.text.toString().toInt(),
            editTextRent.text.toString().toInt(),
            editTextMoneyNow.text.toString().toInt(),
            editTextSalaryFact.text.toString().toInt(),
            editTextSalary.text.toString().toInt(),
            editTextPercentBonus.text.toString().toInt()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        saveMoneyPref(
            editTextMoneyFact.text.toString().toInt(),
            checkBox.isChecked,
            editTextMoneyWished.text.toString().toInt(),
            editTextRent.text.toString().toInt(),
            editTextMoneyNow.text.toString().toInt(),
            editTextSalaryFact.text.toString().toInt(),
            editTextSalary.text.toString().toInt(),
            editTextPercentBonus.text.toString().toInt()
        )
    }

    private fun init() {
        editTextSalary = findViewById(R.id.editTextSalary)
        editTextSalary.setOnEditorActionListener(this)
        editTextPercentBonus = findViewById(R.id.editTextPercentBonus)
        editTextPercentBonus.setOnEditorActionListener(this)

        editTextMoneyFact = findViewById(R.id.editTextMoney)
        editTextMoneyFact.setOnEditorActionListener(this)
        editTextMoneyWished = findViewById(R.id.editTextMoneyWished)
        editTextMoneyWished.setOnEditorActionListener(this)
        editTextRent = findViewById(R.id.editTextRent)
        editTextRent.setOnEditorActionListener(this)
        editTextMoneyNow = findViewById(R.id.editTextMoneyNow)
        editTextMoneyNow.setOnEditorActionListener(this)
        editTextSalaryFact = findViewById(R.id.editTextSalaryFact)
        editTextSalaryFact.setOnEditorActionListener(this)


        checkBox = findViewById(R.id.checkBox)
        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при переключении флажка
        }
        textViewMoneyADay = findViewById(R.id.textViewMoneyADay)
        textViewMoneyAMonth = findViewById(R.id.textViewMoneyAMonth)
        seekBar = findViewById(R.id.seekBar)
        textViewSeekBarProgress = findViewById(R.id.textViewSeekBarProgress)
        textViewPref = findViewById(R.id.textViewPref)
        textViewMoneyAvailable = findViewById(R.id.textViewMoneyAvailable)
        textViewSalaryWithBonus = findViewById(R.id.textViewSalaryWithBonus)
        textViewMinusTax = findViewById(R.id.textViewMinusTax)
        textViewDaysToPay = findViewById(R.id.textViewDaysToPay)
        textViewDaysToPrepay = findViewById(R.id.textViewDaysToPrepay)


        pref2 = getSharedPreferences("Name", Context.MODE_PRIVATE)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
        }
        return false
    }
}