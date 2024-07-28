package com.example.myapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.database.SalaryEntity
import com.example.myapp.databinding.ActivityMainBinding
import com.example.myapp.moneyActivity.MoneyActivity
import com.example.myapp.pastaActivity.PastaActivity
import com.google.android.material.slider.Slider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import com.example.myapp.settingsActivity.SettingsActivity
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    private lateinit var checkBoxMoney: CheckBox

    private var sharedPreferences: SharedPreferences? = null

    private lateinit var binding: ActivityMainBinding

    private val myAppViewModel: MyAppViewModel by viewModels()

    private lateinit var listSalaries: List<SalaryEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val salaryObserver = Observer<List<SalaryEntity>>{salaries ->
            listSalaries = salaries
        }
        myAppViewModel.listSalariesLiveData.observe(this, salaryObserver)

        init()

        val itemList = ArrayList<String>()
        itemList.add("Расходы мои")
        itemList.add("Гречка")
        itemList.add("Макароны")
        itemList.add("Заплата")
        itemList.add("Настройки")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    val intent = Intent(this, MoneyActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(this, CerealActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this, PastaActivity::class.java)
                    startActivity(intent)
                }
                3 -> {
                    val intent = Intent(this, SalaryListActivity::class.java)
                    startActivity(intent)
                }
                4 -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        val savedStateCheckBox = sharedPreferences?.getBoolean("checkBoxMoney", false)
        if (savedStateCheckBox != null) {
            checkBoxMoney.isChecked = savedStateCheckBox
        }

        if (checkBoxMoney.isChecked) {
            val intent = Intent(this, MoneyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("checkBoxMoney", checkBoxMoney.isChecked)
        editor?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val editor = sharedPreferences?.edit()
        editor?.putBoolean("checkBoxMoney", checkBoxMoney.isChecked)
        editor?.apply()
    }

    private fun init() {
        listView = findViewById(R.id.listView)

        checkBoxMoney = findViewById(R.id.checkBoxMoney)

        sharedPreferences = getSharedPreferences("mainPref", Context.MODE_PRIVATE)
    }
}