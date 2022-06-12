package com.example.myapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import java.util.*


class MoneyActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private lateinit var editTextMoney: EditText
    private lateinit var editTextMoneyWished: EditText
    private lateinit var editTextRent: EditText

    private lateinit var textViewMoneyADay: TextView
    private lateinit var textViewMoneyAMonth: TextView
    private lateinit var textViewSeekBarProgress: TextView
    private lateinit var textViewPref: TextView
    private lateinit var textViewMoneyAvailable : TextView


    private lateinit var checkBox: CheckBox
    private lateinit var seekBar: SeekBar

    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        init()

        seekBarsInit()

        //Подгрузка сохраненного состояния
        textViewPref.text = pref?.getInt("money", 0).toString()
        editTextMoney.setText(pref?.getInt("money", 0).toString())
        editTextMoneyWished.setText(pref?.getInt("moneyWished", 0).toString())
        editTextRent.setText(pref?.getInt("rent", 0).toString())

        //Подгрузка сохраненного состояния checkBox при запуске
        val stateOfCheckBox = pref?.getBoolean("checkBox", false)
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
        val rent = (editTextRent.text).toString().toInt()

        //Получение и приведение значения в поле к типу Int
        val money = (editTextMoney.text).toString().toInt()

        // Количество дней в месяце
        val countDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)

        //Денег в день
        val moneyOfDay = if (checkBox.isChecked) {  // Если квартплата уже внесена
            (money - rent) / todayDay              // то вычитаем ее из суммы
        } else {
            money / todayDay                        // Если нет, то просто делим на количество дней
        }
        textViewMoneyADay.text = "Денег в день: $moneyOfDay"    // Выводим в textView

        // Количество денег в месяц с учетом квартплаты
        val moneyMonth = (moneyOfDay * countDayMonth) + rent   // Количество денег в месяц вместе с квартплатой
        textViewMoneyAMonth.text = "Денег в месяц: $moneyMonth"

        // Подсчет и вывод количества денег, которые можно потратить сегодня
        //Получение и приведение значения в поле к типу Int
        val moneyWished = (editTextMoneyWished.text).toString().toInt() // Желаемая сумма в месяц

        if(moneyWished > 0) {
            val moneyAvailable = if (checkBox.isChecked) {
                ((moneyWished - rent) / 31 * todayDay - money) + rent //Сколько можно потратить сегодня (вычет аренды)
            } else {
                (moneyWished - rent) / 31 * todayDay - money //Сколько можно потратить сегодня
            }
            textViewMoneyAvailable.text = "Можно потратить: $moneyAvailable"
        }


    }

    private fun saveMoneyPref(money: Int, checkBox: Boolean, moneyWished: Int, rent: Int) {
        val editor = pref?.edit()
        editor?.putInt("money", money)
        editor?.putBoolean("checkBox", checkBox)
        editor?.putInt("moneyWished", moneyWished)
        editor?.putInt("rent", rent)
        editor?.apply()
    }

    override fun onStop() {
        super.onStop()
        saveMoneyPref(
            editTextMoney.text.toString().toInt(),
            checkBox.isChecked,
            editTextMoneyWished.text.toString().toInt(),
            editTextRent.text.toString().toInt())
    }

    override fun onDestroy() {
        super.onDestroy()
        saveMoneyPref(
            editTextMoney.text.toString().toInt(),
            checkBox.isChecked,
            editTextMoneyWished.text.toString().toInt(),
            editTextRent.text.toString().toInt())
    }

    private fun init() {
        editTextMoney = findViewById(R.id.editTextMoney)
        editTextMoney.setOnEditorActionListener(this)
        editTextMoneyWished = findViewById(R.id.editTextMoneyWished)
        editTextMoneyWished.setOnEditorActionListener(this)
        editTextRent = findViewById(R.id.editTextRent)
        editTextRent.setOnEditorActionListener(this)

        checkBox = findViewById(R.id.checkBox)
        textViewMoneyADay = findViewById(R.id.textViewMoneyADay)
        textViewMoneyAMonth = findViewById(R.id.textViewMoneyAMonth)
        seekBar = findViewById(R.id.seekBar)
        textViewSeekBarProgress = findViewById(R.id.textViewSeekBarProgress)
        textViewPref = findViewById(R.id.textViewPref)
        textViewMoneyAvailable = findViewById(R.id.textViewMoneyAvailable)

        pref = getSharedPreferences("Name", Context.MODE_PRIVATE)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
        }
        return false
    }
}