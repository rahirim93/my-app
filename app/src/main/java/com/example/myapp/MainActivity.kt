package com.example.myapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    private lateinit var checkBoxMoney: CheckBox

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        val itemList = ArrayList<String>()
        itemList.add("Расходы мои")
        itemList.add("Расходы Анины")
        itemList.add("Подсчет шагов")
        itemList.add("Гречка")
        itemList.add("Макароны")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(this, MoneyActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(this, Money2Activity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this, StepsActivity::class.java)
                    startActivity(intent)
                }
                3 -> {
                    val intent = Intent(this, CerealActivity::class.java)
                    startActivity(intent)
                }
                4 -> {
                    val intent = Intent(this, PastaActivity::class.java)
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