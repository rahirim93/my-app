package com.example.myapp.moneyActivity

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEach
import com.example.myapp.databinding.ActivityMoneyBinding
import kotlinx.android.synthetic.main.activity_money.view.*

class SharedHelper(
    private val binding: ActivityMoneyBinding,
    context: Context) {

    private val sharedPreferences = context.getSharedPreferences("MoneyActivity", Context.MODE_PRIVATE)

    private val listView = mutableListOf<View>()

    init {
        makeListView()
    }

    private fun makeListView() {
        binding.root.firstLinearLayout.forEach { it1 ->
            if (it1 is LinearLayout) {
                it1.forEach {
                    if (it is EditText) {
                        listView.add(it)
                    }
                }
            }
        }
        listView.add(binding.checkBox)
    }

    fun sharedSave() {
        val editor = sharedPreferences.edit()
        listView.forEach {
            val key = it.id.toString()
            if (it is EditText) {
                val value = it.text.toString()
                editor.putString(key, value)
            } else if (it is CheckBox) {
                val value = it.isChecked
                editor.putBoolean(key, value)
            }
        }
        editor.apply()
    }

    fun sharedLoad() {
        listView.forEach {
            val key = it.id.toString()
            if (it is EditText) {
                val value = sharedPreferences.getString(key, "0")
                it.setText(value)
            } else if (it is CheckBox) {
                val value = sharedPreferences.getBoolean(key, false)
                it.isChecked = value
            }
        }
    }
}