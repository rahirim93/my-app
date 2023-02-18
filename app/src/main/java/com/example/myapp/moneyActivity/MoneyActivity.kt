package com.example.myapp.moneyActivity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.example.myapp.databinding.ActivityMoneyBinding
import kotlinx.android.synthetic.main.activity_money.view.*
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

/** есть желаемая сумма в месяц. есть количество денег потраченных на сегодняшний день.
 * разница будет количество свободных денег.
 * разница будет различаться в завимости от того выплачена аренда или нет */

class MoneyActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private var pref: SharedPreferences? = null

    private lateinit var binding: ActivityMoneyBinding

    private lateinit var sharedHelper: SharedHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        seekBarsInit()

        sharedHelper = SharedHelper(binding, this)

        sharedHelper.sharedLoad()

        //Подгрузка сохраненного состояния
        binding.textViewPref.text = pref?.getInt("money", 0).toString()

        //Подгрузка сохраненного состояния checkBox при запуске
        val stateOfCheckBox = pref?.getBoolean("checkBox", false)
        if (binding.checkBox.isChecked != stateOfCheckBox) binding.checkBox.toggle()

        countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
    }

    private fun seekBarsInit() {
        binding.seekBar.min = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)                 // Минимальное значение seekBar (дней с начала месяца)
        //seekBar.max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)  // Максимальное значение seekBat (дней в месяце)
        // Максимальное значение seekBar
        if (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 5 >
            Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)) {
            binding.seekBar.max = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        } else {
            binding.seekBar.max = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + 5
        }
        if (binding.seekBar.min == binding.seekBar.max) binding.seekBar.isEnabled = false
        binding.seekBar.progress = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)            // Прогресс seekBar (дней с начала месяца)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textViewSeekBarProgress.text = "$progress"
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
        val rent = getIntFromEditText(binding.editTextRent)                                                         // Арендная плата из EditText
        val moneyFact = getIntFromEditText(binding.editTextMoney) + getIntFromEditText(binding.editTextMoneyTemp)   // Фактическое количество потраченных денег из EditText
        val countDayMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)                          // Количество дней в текущем месяце

        val charge2 = getIntFromEditText(binding.editTextCharge2) // Разовый расход из EditText
        val charge3 = getIntFromEditText(binding.editTextCharge3) // Разовый расход из EditText
        val charge4 = getIntFromEditText(binding.editTextCharge4) // Разовый расход из EditText
        val charge5 = getIntFromEditText(binding.editTextCharge5) // Разовый расход из EditText
        val sumCharges = charge2 + charge3 + charge4 + charge5 // Сумма разовых расходов

        // Среднее количество денег которое тратилось в день до сегодняшнего дня
        // Вычитаем также разовые расходы
        val moneyOfDay = if (binding.checkBox.isChecked) {      // Если квартплата уже внесена
            (moneyFact - sumCharges - rent) / todayDay  // то вычитаем ее из суммы
        } else {
            (moneyFact - sumCharges) / todayDay         // Если нет, то просто делим на количество дней
        }
        binding.textViewMoneyADay.text = "Денег в день: $moneyOfDay"    // Выводим в textView

        val moneyMonth = (moneyOfDay * countDayMonth) + rent + sumCharges   // Количество денег в месяц вместе с квартплатой и разовыми расходами
        binding.textViewMoneyAMonth.text = "Денег в месяц: $moneyMonth"             // Выводим в textView

        // Подсчет и вывод количества денег, которые можно потратить сегодня
        //Получение и приведение значения в поле к типу Int
        val moneyWished = getIntFromEditText(binding.editTextMoneyWished) // Желаемая сумма в месяц
        if(moneyWished > 0) {
            val moneyAvailable = if (binding.checkBox.isChecked) {
                ((moneyWished - rent - sumCharges) / countDayMonth * todayDay - moneyFact) + rent + sumCharges //Сколько можно потратить сегодня (вычет аренды)
            } else {
                (moneyWished - rent - sumCharges) / countDayMonth * todayDay - moneyFact + sumCharges //Сколько можно потратить сегодня
            }
            binding.textViewMoneyAvailable.text = "Можно потратить: $moneyAvailable"
        }

        val salary = getIntFromEditText(binding.editTextSalary)                    // Оклад
        val percentBonus = getIntFromEditText(binding.editTextPercentBonus) + 100  // Процент премии
        val salaryWithBonus = (salary * percentBonus) / 100
        val salaryMinusTax = salaryWithBonus * 87 / 100

        //Дней до конца месяца
        val daysTillEndMonth = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH) -
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        binding.textViewSalaryWithBonus.text = "Зарплата с премией: $salaryWithBonus"
        binding.textViewMinusTax.text = "Зарплата с вычетом налога: $salaryMinusTax"

        binding.textViewDaysToPay.text = "Дней до зарплаты: ${daysToPay()}"
        binding.textViewDaysToPrepay.text = "Дней до аванса: ${daysToPrePay()} \n" +
                "Дней до конца месяца: $daysTillEndMonth"
    }

    override fun onStop() {
        super.onStop()
        sharedHelper.sharedSave()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedHelper.sharedSave()
    }

    private fun init() {
        pref = getSharedPreferences("Name", Context.MODE_PRIVATE)

        // Перебираем элементы layout, назначаем слуатели на клавитуру для EditText
        binding.root.firstLinearLayout.forEach { it1 ->
            if (it1 is LinearLayout) {
                it1.forEach {
                    if (it is EditText) {
                        it.setOnEditorActionListener(this@MoneyActivity)
                    }
                }
            }
        }

        binding.editTextMoney.apply {
            requestFocus() // Фокусируемся на данном поле
            setSelection(this.length()) // Курсор перемещаем в конец
            postDelayed({ // Открываем клавиатуру
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(binding.editTextMoney, InputMethodManager.SHOW_IMPLICIT)
            }, 500) // Делаем это с задержкой (без задержки не работает)
        }

        binding.checkBox.setOnCheckedChangeListener { _, _ ->
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при переключении флажка
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
        }
        v?.clearFocus()
        return false
    }
}