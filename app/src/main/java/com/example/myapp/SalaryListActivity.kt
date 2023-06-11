package com.example.myapp

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.database.SalaryEntity
import com.example.myapp.databinding.ActivitySalariesBinding
import java.util.*

class SalaryListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalariesBinding

    private val myAppViewModel: MyAppViewModel by viewModels()


    private lateinit var salaryRecyclerView: RecyclerView
    private var adapter: SalaryAdapter? = SalaryAdapter(emptyList())

    private lateinit var buttonInsert: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivitySalariesBinding.inflate(layoutInflater)
        setContentView(binding.root)



        buttonInsert = findViewById(R.id.button_insert)
        buttonInsert.setOnClickListener {
            myAppViewModel.addSalary(SalaryEntity())
        }

        salaryRecyclerView = findViewById(R.id.salaries_recycler_view)
        salaryRecyclerView.layoutManager = LinearLayoutManager(this)
        salaryRecyclerView.adapter = adapter

        val salaryObserver = Observer<List<SalaryEntity>>{salaries ->
            updateUI(salaries)
        }
        myAppViewModel.listSalariesLiveData.observe(this, salaryObserver)
    }

    private fun updateUI(salaries: List<SalaryEntity>){
        adapter = SalaryAdapter(salaries)
        salaryRecyclerView.adapter = adapter
    }

    private inner class SalaryHolder(view: View): RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var salary: SalaryEntity

        private val yearTextView: TextView = itemView.findViewById(R.id.salary_year)
        private val monthTextView: TextView = itemView.findViewById(R.id.salary_month)
        private val salaryTextView: TextView = itemView.findViewById(R.id.salary)
        private val expensesTextView: TextView = itemView.findViewById(R.id.expenses)
        private val profitTextView: TextView = itemView.findViewById(R.id.profit)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(salary: SalaryEntity){
            this.salary = salary
            yearTextView.text = "Год:\t\t\t\t\t\t\t" + this.salary.date.get(Calendar.YEAR).toString()
            // Месяц. +1 потому что первый месяц в классе Calendar январь имеет номер 0
            monthTextView.text = "Месяц:\t\t\t\t" + (this.salary.date.get(Calendar.MONTH) + 1).toString()
            salaryTextView.text = "Зарплата:\t" + this.salary.salary.toString()
            expensesTextView.text = "Расходы:\t\t" +  this.salary.expenses.toString()
            // Вывод дохода
            var exp = 0
            if (this.salary.expenses == null) {
                exp = 0
            } else {
                exp = this.salary.expenses!!
            }
            var profit = this.salary.salary - exp
            profitTextView.text = "Доход:\t\t\t\t" + profit

        }

        override fun onClick(v: View?) {
            val intent = SalaryActivity.newIntent(
                this@SalaryListActivity,
                salary.id.toString())
            startActivity(intent)
        }
    }

    private inner class SalaryAdapter(var salaries: List<SalaryEntity>)
        : RecyclerView.Adapter<SalaryHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaryHolder {
            val view = layoutInflater.inflate(R.layout.list_item_salary, parent, false)
            return SalaryHolder(view)
        }

        override fun getItemCount() = salaries.size

        override fun onBindViewHolder(holder: SalaryHolder, position: Int) {
            val salary = salaries[position]
            holder.bind(salary)
        }
    }
}