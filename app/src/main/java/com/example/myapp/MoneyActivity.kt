package com.example.myapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.myapp.databinding.ActivityMoneyBinding
import com.example.myapp.moneyActivity.daysToPay
import com.example.myapp.moneyActivity.daysToPrePay
import com.example.myapp.moneyActivity.getTextFromEditText
import java.util.*


/** Расчет больничного.
 *      1 Нужно определеить расчетный период для исчисления среднего заработка. Расчетный период
 * для исчисления среднего заработка — 12 календарных месяцев, предшествующих периоду, в течение
 * которого за работником сохраняется средняя заработная плата
 * Сделать расчет расчетного периода за последние 12 месяцев для этого:
 * 1) нужно выбрать день в месяце.
 * 2) выбрать 12 месяцев до этого месяца
 * 3) взять оттуда зарлпты и посчитать средний заработок по формуле / 12 месяцев / 29,3
 */


// Вынести отдельную функцию для получения значения с editText
// Если editText пустой, то при сохранения в shared preferences то вылетает исключение
// Вынести в отдельную функцию сохранение состояния
// Если в editText ничего нет, то при закрытии приложения выелтает исключение, исправить
// Во всех editText обработать случай если поле пустое, т.к. вылетает исключение

/**
 * слишком много edittext.чтобы укоростить код, нужно создать массив edittext и проводить операции по сохранению и извлечению
 * из SharedPreferences с массивом. подумать о реализации
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

    private lateinit var binding: ActivityMoneyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        seekBarsInit()

        //Подгрузка сохраненного состояния
        textViewPref.text = pref?.getInt("money", 0).toString()
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

    @SuppressLint("SetTextI18n")
    private fun countMoney(todayDay: Int) {
        val rent = getTextFromEditText(binding.editTextRent)                                        // Арендная плата из EditText
        val moneyFact = getTextFromEditText(binding.editTextMoney)                              // Фактическое количество потраченных денег из EditText
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
        val moneyWished = getTextFromEditText(binding.editTextMoneyWished) // Желаемая сумма в месяц
        if(moneyWished > 0) {
            val moneyAvailable = if (checkBox.isChecked) {
                ((moneyWished - rent - sumCharges) / countDayMonth * todayDay - moneyFact) + rent + sumCharges //Сколько можно потратить сегодня (вычет аренды)
            } else {
                (moneyWished - rent - sumCharges) / countDayMonth * todayDay - moneyFact + sumCharges //Сколько можно потратить сегодня
            }
            textViewMoneyAvailable.text = "Можно потратить: $moneyAvailable"
        }

        val salary = getTextFromEditText(binding.editTextSalary)                    // Оклад
        val percentBonus = getTextFromEditText(binding.editTextPercentBonus) + 100  // Процент премии
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
            binding.editTextMoney.text.toString().toInt(),
            checkBox.isChecked,
            binding.editTextMoneyWished.text.toString().toInt(),
            binding.editTextRent.text.toString().toInt(),
            binding.editTextMoneyNow.text.toString().toInt(),
            binding.editTextSalaryFact.text.toString().toInt(),
            binding.editTextSalary.text.toString().toInt(),
            binding.editTextPercentBonus.text.toString().toInt(),
            getTextFromEditText(editTextCharge2),
            getTextFromEditText(editTextCharge3),
            getTextFromEditText(editTextCharge4),
            getTextFromEditText(editTextCharge5)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        saveMoneyPref(
            binding.editTextMoney.text.toString().toInt(),
            checkBox.isChecked,
            binding.editTextMoneyWished.text.toString().toInt(),
            binding.editTextRent.text.toString().toInt(),
            binding.editTextMoneyNow.text.toString().toInt(),
            binding.editTextSalaryFact.text.toString().toInt(),
            binding.editTextSalary.text.toString().toInt(),
            binding.editTextPercentBonus.text.toString().toInt(),
            getTextFromEditText(editTextCharge2),
            getTextFromEditText(editTextCharge3),
            getTextFromEditText(editTextCharge4),
            getTextFromEditText(editTextCharge5)
        )
    }

    private fun init() {
        pref = getSharedPreferences("Name", Context.MODE_PRIVATE)

        binding.editTextSalary.apply {
            setOnEditorActionListener(this@MoneyActivity)
            setText(pref?.getInt("salary", 0).toString())
        }

        binding.editTextPercentBonus.apply {
            setOnEditorActionListener(this@MoneyActivity)
            setText(pref?.getInt("percentBonus", 0).toString())
        }

        binding.editTextMoney.apply {
            setOnEditorActionListener(this@MoneyActivity) // Назначаем слушатель
            setText(pref?.getInt("money", 0).toString()) // Восстанавливаем сохраненное состояние
            requestFocus() // Фокусируемся на данном поле
            setSelection(this.length()) // Курсор перемещаем в конец
            postDelayed({ // Открываем клавиатуру
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(binding.editTextMoney, InputMethodManager.SHOW_IMPLICIT)
            }, 500) // Делаем это с задержкой (без задержки не работает)
        }

        binding.editTextMoneyWished.apply {
            setOnEditorActionListener(this@MoneyActivity)
            setText(pref?.getInt("moneyWished", 0).toString())
        }

        binding.editTextRent.apply {
            setOnEditorActionListener(this@MoneyActivity)
            setText(pref?.getInt("rent", 0).toString())
        }

        binding.editTextMoneyNow.apply {
            setOnEditorActionListener(this@MoneyActivity)
            setText(pref?.getInt("moneyNow", 0).toString())
        }

        binding.editTextSalaryFact.apply {
            setOnEditorActionListener(this@MoneyActivity)
            setText(pref?.getInt("salaryFact", 0).toString())
        }

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
        checkBox.setOnCheckedChangeListener { _, _ ->
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
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
        }
        v?.clearFocus()
        return false
    }
}