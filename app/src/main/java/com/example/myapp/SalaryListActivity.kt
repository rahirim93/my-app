package com.example.myapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.database.SalaryEntity
import java.util.*

class SalaryListActivity : AppCompatActivity() {

    private val myAppViewModel: MyAppViewModel by lazy {
        ViewModelProviders.of(this).get(MyAppViewModel::class.java)
    }

    private lateinit var salaryRecyclerView: RecyclerView
    private var adapter: SalaryAdapter? = SalaryAdapter(emptyList())

    private lateinit var buttonInsert: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_salaries)

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

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(salary: SalaryEntity){
            this.salary = salary
            yearTextView.text = "Год:\t\t\t\t\t\t" + this.salary.date.get(Calendar.YEAR).toString()
            // Месяц. +1 потому что первый месяц в классе Calendar январь имеет номер 0
            monthTextView.text = "Месяц:\t\t\t\t" + (this.salary.date.get(Calendar.MONTH) + 1).toString()
            salaryTextView.text = "Зарплата:\t\t" + this.salary.salary.toString()
        }

        override fun onClick(v: View?) {
            val intent = SalaryActivity.newIntent(
                this@SalaryListActivity,
                salary.id.toString(),
                salary.date.timeInMillis,
                salary.salary)
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