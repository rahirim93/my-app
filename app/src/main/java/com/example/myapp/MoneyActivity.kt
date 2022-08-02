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


/** Расчет больничного.
 *      1 Нужно определеить расчетный период для исчисления среднего заработка. Расчетный период
 * для исчисления среднего заработка — 12 календарных месяцев, предшествующих периоду, в течение
 * которого за работником сохраняется средняя заработная плата
 *
 */


// Вынести отдельную функцию для получения значения с editText
// Если editText пустой, то при сохранения в shared preferences то вылетает исключение
// Вынести в отдельную функцию сохранение состояния
// Если в editText ничего нет, то при закрытии приложения выелтает исключение, исправить
// Во всех editText обработать случай если поле пустое, т.к. вылетает исключение

/**
 * слишком много edittext.чтобы укоростить код, нужно создать массив edittext и проводить операции по сохранению и извлечению
 * из sharedpreferences с массивом. подумать о реализации
 *
 *
 *
 * есть желаемая сумма в месяц. есть количество денег потраченных на сегодняшний день.
 * разница будет количество свободных денег.
 * разница будет различаться в завимости от того выплачена аренда или нет
 *
 *
 */

class MoneyActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private lateinit var editTextSalary: EditText       // Поле для ввода оклада
    private lateinit var editTextPercentBonus: EditText // Поле для ввода процента премии

    private lateinit var editTextMoneyFact: EditText
    private lateinit var editTextMoneyWished: EditText
    private lateinit var editTextRent: EditText
    private lateinit var editTextMoneyNow: EditText
    private lateinit var editTextSalaryFact: EditText

    // Edittext разовых расходов
    private lateinit var editTextCharge2: EditText
    private lateinit var editTextCharge3: EditText
    private lateinit var editTextCharge4: EditText
    private lateinit var editTextCharge5: EditText


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

    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money)

        init()

        seekBarsInit()

        //Подгрузка сохраненного состояния
        textViewPref.text = pref?.getInt("money", 0).toString()
        editTextMoneyFact.setText(pref?.getInt("money", 0).toString())
        editTextMoneyWished.setText(pref?.getInt("moneyWished", 0).toString())
        editTextRent.setText(pref?.getInt("rent", 0).toString())
        editTextMoneyNow.setText(pref?.getInt("moneyNow", 0).toString())
        editTextSalaryFact.setText(pref?.getInt("salaryFact", 0).toString())
        editTextSalary.setText(pref?.getInt("salary", 0).toString())
        editTextPercentBonus.setText(pref?.getInt("percentBonus", 0).toString())
        // Подгрузка значений EditText разовых расходов
        editTextCharge2.setText(pref?.getInt("charge2", 0).toString())
        editTextCharge3.setText(pref?.getInt("charge3", 0).toString())
        editTextCharge4.setText(pref?.getInt("charge4", 0).toString())
        editTextCharge5.setText(pref?.getInt("charge5", 0).toString())


        //Подгрузка сохраненного состояния checkBox при запуске
        val stateOfCheckBox = pref?.getBoolean("checkBox", false)
        if (checkBox.isChecked != stateOfCheckBox) checkBox.toggle()

        countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
    }

    private fun seekBarsInit() {
        seekBar.min = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)                 // Минимальное значение seekBar (дней с начала месяца)
        //seekBar.max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)  // Максимальное значение seekBat (дней в месяце)
        // Максимальное значение seekBar
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 5 >
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)) {
            seekBar.max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        } else {
            seekBar.max = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + 5
        }
        if (seekBar.min == seekBar.max) seekBar.isEnabled = false
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
        val rent = getTextFromEditText(editTextRent)                                        // Арендная плата из EditText
        val moneyFact = getTextFromEditText(editTextMoneyFact)                              // Фактическое количество потраченных денег из EditText
        val countDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)  // Количество дней в текущем месяце

        val charge2 = getTextFromEditText(editTextCharge2) // Разовый расход из EditText
        val charge3 = getTextFromEditText(editTextCharge3) // Разовый расход из EditText
        val charge4 = getTextFromEditText(editTextCharge4) // Разовый расход из EditText
        val charge5 = getTextFromEditText(editTextCharge5) // Разовый расход из EditText
        val sumCharges = charge2 + charge3 + charge4 + charge5 // Сумма разовых расходов

        // Среднее количество денег которое тратилось в день до сегодняшнего дня
        // Вычитаем также разовые расходы
        val moneyOfDay = if (checkBox.isChecked) {      // Если квартплата уже внесена
            (moneyFact - sumCharges - rent) / todayDay  // то вычитаем ее из суммы
        } else {
            (moneyFact - sumCharges) / todayDay         // Если нет, то просто делим на количество дней
        }
        textViewMoneyADay.text = "Денег в день: $moneyOfDay"    // Выводим в textView

        val moneyMonth = (moneyOfDay * countDayMonth) + rent + sumCharges   // Количество денег в месяц вместе с квартплатой и разовыми расходами
        textViewMoneyAMonth.text = "Денег в месяц: $moneyMonth"             // Выводим в textView

        // Подсчет и вывод количества денег, которые можно потратить сегодня
        //Получение и приведение значения в поле к типу Int
        val moneyWished = getTextFromEditText(editTextMoneyWished) // Желаемая сумма в месяц
        if(moneyWished > 0) {
            val moneyAvailable = if (checkBox.isChecked) {
                ((moneyWished - rent - sumCharges) / countDayMonth * todayDay - moneyFact) + rent + sumCharges //Сколько можно потратить сегодня (вычет аренды)
            } else {
                (moneyWished - rent - sumCharges) / countDayMonth * todayDay - moneyFact + sumCharges //Сколько можно потратить сегодня
            }
            textViewMoneyAvailable.text = "Можно потратить: $moneyAvailable"
        }

        val salary = getTextFromEditText(editTextSalary)                    // Оклад
        val percentBonus = getTextFromEditText(editTextPercentBonus) + 100  // Процент премии
        val salaryWithBonus = (salary * percentBonus) / 100
        val salaryMinusTax = salaryWithBonus * 87 / 100

        //Дней до конца месяца
        val daysTillEndMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) -
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        textViewSalaryWithBonus.text = "Зарплата с премией: $salaryWithBonus"
        textViewMinusTax.text = "Зарплата с вычетом налога: $salaryMinusTax"

        textViewDaysToPay.text = "Дней до зарплаты: ${daysToPay()}"
        textViewDaysToPrepay.text = "Дней до аванса: ${daysToPrePay()} \n" +
                "Дней до конца месяца: $daysTillEndMonth"
    }

    private fun saveMoneyPref(money: Int, checkBox: Boolean, moneyWished: Int, rent: Int,
                              moneyNow: Int, salaryFact: Int, salary: Int, percentBonus: Int,
                              charge2: Int, charge3: Int, charge4: Int, charge5: Int) {
        val editor = pref?.edit()
        editor?.putInt("money", money)
        editor?.putBoolean("checkBox", checkBox)
        editor?.putInt("moneyWished", moneyWished)
        editor?.putInt("rent", rent)
        editor?.putInt("moneyNow", moneyNow)
        editor?.putInt("salaryFact", salaryFact)
        editor?.putInt("salary", salary)
        editor?.putInt("percentBonus", percentBonus)
        // Сохраненине значений EditText разовых расходов
        editor?.putInt("charge2", charge2)
        editor?.putInt("charge3", charge3)
        editor?.putInt("charge4", charge4)
        editor?.putInt("charge5", charge5)
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
            editTextPercentBonus.text.toString().toInt(),
            getTextFromEditText(editTextCharge2),
            getTextFromEditText(editTextCharge3),
            getTextFromEditText(editTextCharge4),
            getTextFromEditText(editTextCharge5)
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
            editTextPercentBonus.text.toString().toInt(),
            getTextFromEditText(editTextCharge2),
            getTextFromEditText(editTextCharge3),
            getTextFromEditText(editTextCharge4),
            getTextFromEditText(editTextCharge5)
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

        // Edittext разовых расходов
        editTextCharge2 = findViewById(R.id.editTextCharge2)
        editTextCharge2.setOnEditorActionListener(this)
        editTextCharge3 = findViewById(R.id.editTextCharge3)
        editTextCharge3.setOnEditorActionListener(this)
        editTextCharge4 = findViewById(R.id.editTextCharge4)
        editTextCharge4.setOnEditorActionListener(this)
        editTextCharge5 = findViewById(R.id.editTextCharge5)
        editTextCharge5.setOnEditorActionListener(this)

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


        pref = getSharedPreferences("Name", Context.MODE_PRIVATE)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
        }
        return false
    }
}