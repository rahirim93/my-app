package com.example.myapp.moneyActivity

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.forEach
import com.example.myapp.databinding.ActivityMoneyBinding
import kotlinx.android.synthetic.main.activity_money.view.*

class SharedHelper(
    private val binding: ActivityMoneyBinding,
    context: Context) {

    private val sharedPreferences = context.getSharedPreferences("name", Context.MODE_PRIVATE)

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
    }

    fun sharedSave() {
        val editor = sharedPreferences.edit()
        listView.forEach {
            val key = it.id.toString()
            val value = (it as EditText).text.toString()
            editor.putString(key, value)
        }
        editor.apply()
    }

    fun sharedLoad() {
        listView.forEach {
            val key = it.id.toString()
            val value = sharedPreferences.getString(key, "0")
            (it as EditText).setText(value)
        }
    }
}