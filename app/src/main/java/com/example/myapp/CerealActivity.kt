package com.example.myapp

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class CerealActivity : AppCompatActivity() {

    private lateinit var textViewWater: TextView
    private lateinit var textViewCereal: TextView
    private lateinit var editTextWater: EditText
    private lateinit var editTextCereal: EditText
    private lateinit var buttonWater: Button
    private lateinit var buttonCereal: Button

    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cereal)

        init()
    }

    fun buttonWater(view: android.view.View) {

        val cereal = (editTextWater.text).toString().toInt()

        val water = (cereal * 1.555).toString()

        if (cereal != null) {
            textViewWater.text = "Количество воды: $water"
        }
    }
    fun buttonCereal(view: android.view.View) {

        val cereal2 = (editTextCereal.text).toString().toInt()

        val cerealReady = (cereal2 * 2.4).toInt().toString()

        if (cereal2 != null) {
            textViewCereal.text = cerealReady
        }
    }

    private fun init() {
        textViewWater = findViewById(R.id.textView)
        textViewCereal = findViewById(R.id.textView2)
        editTextWater = findViewById(R.id.editTextNumber2)
        editTextCereal = findViewById(R.id.editTextNumber3)
        buttonWater = findViewById(R.id.button2)
        buttonCereal = findViewById(R.id.button3)
    }


}