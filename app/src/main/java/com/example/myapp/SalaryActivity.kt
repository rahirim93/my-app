package com.example.myapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.example.myapp.database.SalaryEntity
import java.util.*

private const val EXTRA_SALARY_ID = "com.example.myapp.ID"
private const val EXTRA_SALARY_DATE = "com.example.myapp.DATE"
private const val EXTRA_SALARY_SALARY = "com.example.myapp.SALARY"

class SalaryActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private val salaryViewModel: SalaryViewModel by lazy {
        ViewModelProviders.of(this).get(SalaryViewModel::class.java)
    }

    private lateinit var buttonDelete: Button

    private var salaryItem = SalaryEntity()

    private lateinit var editTextYear: EditText
    private lateinit var editTextMonth: EditText
    private lateinit var editTextSalary: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)


        init()


        // Получение ID из интента
        salaryItem.id = UUID.fromString(intent.getStringExtra(EXTRA_SALARY_ID))
        // Получение даты из интента
        var calendar = Calendar.getInstance()
        calendar.timeInMillis = intent.getLongExtra(EXTRA_SALARY_DATE, 0)
        salaryItem.date = calendar
        // Получение зарплаты из интента
        salaryItem.salary = intent.getIntExtra(EXTRA_SALARY_SALARY, 0)

        // Заполнение полей editText значениями
        // Год
        val year = salaryItem.date.get(Calendar.YEAR).toString()
        // Месяц. +1 потому что первый месяц в классе Calendar январь имеет номер 0
        val month = (salaryItem.date.get(Calendar.MONTH) + 1).toString()
        // Зарплата
        val salary = salaryItem.salary.toString()
        // Заполнение editText
        editTextYear.setText(year)
        editTextMonth.setText(month)
        editTextSalary.setText(salary)

        log("ID: ${salaryItem.id} \t " +
                "Месяц: ${salaryItem.date.get(Calendar.MONTH)} \t " +
                "Год: ${salaryItem.date.get(Calendar.YEAR)} \t " +
                "Зарплата: ${salaryItem.salary} \t " +
                "Дата: ${formateCalendar(salaryItem.date)}")
    }

    private fun init() {
        editTextYear = findViewById(R.id.editTextYear)
        editTextYear.setOnEditorActionListener(this)

        editTextMonth = findViewById(R.id.editTextMonth)
        editTextMonth.setOnEditorActionListener(this)

        editTextSalary = findViewById(R.id.editTextSalary)
        editTextSalary.setOnEditorActionListener(this)

        buttonDelete = findViewById(R.id.button_delete)
        buttonDelete.setOnClickListener {
            salaryViewModel.deleteSalary(salaryItem)
            finish()
        }
    }

    companion object {
        fun newIntent(packageContext: Context, salaryID: String, salaryDate: Long, salary: Int): Intent {
            return Intent(packageContext, SalaryActivity::class.java).apply {
                putExtra(EXTRA_SALARY_ID, salaryID)
                putExtra(EXTRA_SALARY_DATE, salaryDate)
                putExtra(EXTRA_SALARY_SALARY, salary)
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            //countMoney(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) // Расчет при запуске приложения
        }
        if (v != null) {
            if (v.id == editTextYear.id) {
                if (editTextYear.text.toString() != "") {
                    salaryItem.date.set(Calendar.YEAR, editTextYear.text.toString().toInt())
                }
            }
            if (v.id == editTextMonth.id) {
                if (editTextMonth.text.toString() != "") {
                    // Месяц. -1 потому что первый месяц в классе Calendar январь имеет номер 0
                    salaryItem.date.set(Calendar.MONTH, editTextMonth.text.toString().toInt() - 1)
                }
            }
            if (v.id == editTextSalary.id) {
                if (editTextSalary.text.toString() != "") {
                    salaryItem.salary = editTextSalary.text.toString().toInt()
                }
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        salaryViewModel.updateSalary(salaryItem)
    }
}