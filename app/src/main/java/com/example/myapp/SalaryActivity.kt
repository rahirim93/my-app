package com.example.myapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.myapp.database.SalaryEntity
import com.example.myapp.databinding.ActivitySalaryBinding
import java.util.*


private const val EXTRA_SALARY_ID = "com.example.myapp.ID"
//private const val EXTRA_SALARY_DATE = "com.example.myapp.DATE"
//private const val EXTRA_SALARY_SALARY = "com.example.myapp.SALARY"

class SalaryActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private val salaryViewModel: SalaryViewModel by viewModels()

    private lateinit var buttonDelete: Button

    private lateinit var editTextYear: EditText
    private lateinit var editTextMonth: EditText
    private lateinit var editTextSalary: EditText

    private var salaryItem : SalaryEntity? = null

    private lateinit var binding: ActivitySalaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salary)

        binding = ActivitySalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        val salaryId = intent.getStringExtra(EXTRA_SALARY_ID)
        val salaryObserver = Observer<SalaryEntity?> { salaryFromDB ->
            salaryItem = salaryFromDB
            // Заполнение полей editText значениями
            // Год
            val year = salaryItem?.date?.get(Calendar.YEAR).toString()
            // Месяц. +1 потому что первый месяц в классе Calendar январь имеет номер 0
            val month = (salaryItem?.date!!.get(Calendar.MONTH) + 1).toString()
            // Зарплата
            val salary = salaryItem!!.salary.toString()
            // Заполнение editText
            val expenses = salaryItem!!.expenses.toString()
            editTextYear.setText(year)
            editTextMonth.setText(month)
            editTextSalary.setText(salary)
            binding.editTextExpenses.setText(expenses)

        }
        salaryViewModel.getSalaryById(salaryId!!).observe(this, salaryObserver)
    }

    private fun init() {
        editTextYear = findViewById(R.id.editTextYear)
        editTextYear.setOnEditorActionListener(this)

        editTextMonth = findViewById(R.id.editTextMonth)
        editTextMonth.setOnEditorActionListener(this)

        editTextSalary = findViewById(R.id.editTextSalary)
        editTextSalary.setOnEditorActionListener(this)

        binding.editTextExpenses.setOnEditorActionListener(this)

        buttonDelete = findViewById(R.id.button_delete)
        buttonDelete.setOnClickListener {
            salaryViewModel.deleteSalary(salaryItem)
            finish()
        }
    }

    companion object {
        fun newIntent(packageContext: Context, salaryID: String): Intent {
            return Intent(packageContext, SalaryActivity::class.java).apply {
                putExtra(EXTRA_SALARY_ID, salaryID)
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
                    salaryItem?.date?.set(Calendar.YEAR, editTextYear.text.toString().toInt())
                }
            }
            if (v.id == editTextMonth.id) {
                if (editTextMonth.text.toString() != "") {
                    // Месяц. -1 потому что первый месяц в классе Calendar январь имеет номер 0
                    salaryItem?.date?.set(Calendar.MONTH, editTextMonth.text.toString().toInt() - 1)
                }
            }
            if (v.id == editTextSalary.id) {
                if (editTextSalary.text.toString() != "") {
                    salaryItem?.salary = editTextSalary.text.toString().toInt()
                    Toast.makeText(this, "done", Toast.LENGTH_SHORT).show()

                }
            }
            if (v.id == binding.editTextExpenses.id) {
                if (binding.editTextExpenses.text.toString() != "") {
                    salaryItem?.expenses = binding.editTextExpenses.text.toString().toInt()
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