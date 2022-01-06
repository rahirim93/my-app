package com.example.myapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()

        var itemList = ArrayList<String>()
        itemList.add("Расходы")
        itemList.add("Подсчет шагов")
        itemList.add("Гречка")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    val intent = Intent(this, MoneyActivity::class.java)
                    startActivity(intent)
                }
                1 -> {
                    val intent = Intent(this, StepsActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this, CerealActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun init() {
        listView = findViewById(R.id.listView)
    }
}