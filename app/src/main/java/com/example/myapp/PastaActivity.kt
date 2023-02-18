package com.example.myapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.moneyActivity.getIntFromEditText
import kotlinx.android.synthetic.main.activity_pasta.*

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

    @SuppressLint("SetTextI18n")
    private fun count() {
        var proportion = 0.0

        if (getIntFromEditText(editTextCooked) != 0 && getIntFromEditText(editTextUncooked) != 0) {
            val cooked = if (checkBoxPan.isChecked) {
                getIntFromEditText(editTextCooked).toDouble() - 672.0
            } else {
                getIntFromEditText(editTextCooked).toDouble()
            }
            val uncooked = getIntFromEditText(editTextUncooked)
            proportion = cooked / uncooked
            textViewProportion.text = "Готовые/сырые $proportion"
        }

        //Первая порция
        val firstPortion = getIntFromEditText(editTextFirstPortion).toDouble() * proportion
        textViewFirstPortion.text = "$firstPortion"
        //Вторая порция
        val secondPortion = getIntFromEditText(editTextSecondPortion).toDouble() * proportion
        textViewSecondPortion.text = "$secondPortion"
        //Третья порция
        val thirdPortion = getIntFromEditText(editTextThirdPortion).toDouble() * proportion
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

        checkBoxPan.setOnCheckedChangeListener { _, _ ->
            count()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            count()
        }
        return false
    }

    private fun savePreferences(cooked: Int, uncooked: Int, firstPortion: Int, secondPortion: Int, thirdPortion: Int, isTherePan: Boolean) {
        val editor = sharedPreferences?.edit()
        editor?.putInt("cooked", cooked)
        editor?.putInt("uncooked", uncooked)
        editor?.putInt("firstPortion", firstPortion)
        editor?.putInt("secondPortion", secondPortion)
        editor?.putInt("thirdPortion", thirdPortion)
        editor?.putBoolean("isTherePan", isTherePan)
        editor?.apply()
    }

    private fun loadSharedPreferences() {
        editTextCooked.setText(sharedPreferences?.getInt("cooked", 0).toString())
        editTextUncooked.setText(sharedPreferences?.getInt("uncooked", 0).toString())
        editTextFirstPortion.setText(sharedPreferences?.getInt("firstPortion", 0).toString())
        editTextSecondPortion.setText(sharedPreferences?.getInt("secondPortion", 0).toString())
        editTextThirdPortion.setText(sharedPreferences?.getInt("thirdPortion", 0).toString())
        checkBoxPan.isChecked = sharedPreferences?.getBoolean("isTherePan", true)!!
    }

    override fun onStop() {
        super.onStop()
        savePreferences(
            getIntFromEditText(editTextCooked),
            getIntFromEditText(editTextUncooked),
            getIntFromEditText(editTextFirstPortion),
            getIntFromEditText(editTextSecondPortion),
            getIntFromEditText(editTextThirdPortion),
            checkBoxPan.isChecked
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        savePreferences(
            getIntFromEditText(editTextCooked),
            getIntFromEditText(editTextUncooked),
            getIntFromEditText(editTextFirstPortion),
            getIntFromEditText(editTextSecondPortion),
            getIntFromEditText(editTextThirdPortion),
            checkBoxPan.isChecked
        )
    }
}