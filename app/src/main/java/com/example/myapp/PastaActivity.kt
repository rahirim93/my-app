package com.example.myapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.example.myapp.moneyActivity.getTextFromEditText
import java.util.*

class PastaActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private lateinit var editTextUncooked: EditText
    private lateinit var editTextCooked: EditText

    private lateinit var textViewProportion: TextView

    private lateinit var editTextFirstPortion: EditText
    private lateinit var editTextSecondPortion: EditText
    private lateinit var editTextThirdPortion: EditText



    private lateinit var textViewFirstPortion: TextView
    private lateinit var textViewSecondPortion: TextView
    private lateinit var textViewThirdPortion: TextView

    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasta)

        init()

        loadSharedPreferences()

        count()
    }

    private fun count() {
        var proportion = 0.0

        if (getTextFromEditText(editTextCooked) != 0 && getTextFromEditText(editTextUncooked) != 0) {
            proportion = getTextFromEditText(editTextCooked).toDouble() / getTextFromEditText(editTextUncooked).toDouble()
            textViewProportion.text = "Готовые/сырые $proportion"
        }

        //Первая порция
        var firstPortion = getTextFromEditText(editTextFirstPortion).toDouble() * proportion
        textViewFirstPortion.text = "$firstPortion"
        //Вторая порция
        var secondPortion = getTextFromEditText(editTextSecondPortion).toDouble() * proportion
        textViewSecondPortion.text = "$secondPortion"
        //Третья порция
        var thirdPortion = getTextFromEditText(editTextThirdPortion).toDouble() * proportion
        textViewThirdPortion.text = "$thirdPortion"
    }

    private fun init() {
        editTextUncooked = findViewById(R.id.editTextUncooked)
        editTextUncooked.setOnEditorActionListener(this)
        editTextUncooked.setText("0")
        editTextCooked = findViewById(R.id.editTextCooked)
        editTextCooked.setOnEditorActionListener(this)
        editTextCooked.setText("0")

        textViewProportion = findViewById(R.id.textViewProportion)

        editTextFirstPortion = findViewById(R.id.editTextFirstPortion)
        editTextFirstPortion.setOnEditorActionListener(this)
        editTextFirstPortion.setText("0")
        editTextSecondPortion = findViewById(R.id.editTextSecondPortion)
        editTextSecondPortion.setOnEditorActionListener(this)
        editTextSecondPortion.setText("0")
        editTextThirdPortion = findViewById(R.id.editTextThirdPortion)
        editTextThirdPortion.setOnEditorActionListener(this)
        editTextThirdPortion.setText("0")

        textViewFirstPortion = findViewById(R.id.textViewFirstPortion)
        textViewSecondPortion = findViewById(R.id.textViewSecondPortion)
        textViewThirdPortion = findViewById(R.id.textViewThirdPortion)

        sharedPreferences = getSharedPreferences("Name", Context.MODE_PRIVATE)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            count()
        }
        return false
    }

    private fun savePreferences(cooked: Int, uncooked: Int, firstPortion: Int, secondPortion: Int, thirdPortion: Int) {
        val editor = sharedPreferences?.edit()
        editor?.putInt("cooked", cooked)
        editor?.putInt("uncooked", uncooked)
        editor?.putInt("firstPortion", firstPortion)
        editor?.putInt("secondPortion", secondPortion)
        editor?.putInt("thirdPortion", thirdPortion)
        editor?.apply()
    }

    private fun loadSharedPreferences() {
        editTextCooked.setText(sharedPreferences?.getInt("cooked", 0).toString())
        editTextUncooked.setText(sharedPreferences?.getInt("uncooked", 0).toString())
        editTextFirstPortion.setText(sharedPreferences?.getInt("firstPortion", 0).toString())
        editTextSecondPortion.setText(sharedPreferences?.getInt("secondPortion", 0).toString())
        editTextThirdPortion.setText(sharedPreferences?.getInt("thirdPortion", 0).toString())
    }

    override fun onStop() {
        super.onStop()
        savePreferences(
            getTextFromEditText(editTextCooked),
            getTextFromEditText(editTextUncooked),
            getTextFromEditText(editTextFirstPortion),
            getTextFromEditText(editTextSecondPortion),
            getTextFromEditText(editTextThirdPortion)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        savePreferences(
            getTextFromEditText(editTextCooked),
            getTextFromEditText(editTextUncooked),
            getTextFromEditText(editTextFirstPortion),
            getTextFromEditText(editTextSecondPortion),
            getTextFromEditText(editTextThirdPortion)
        )
    }
}