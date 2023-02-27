package com.example.myapp.pastaActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.databinding.ActivityPastaBinding
import com.example.myapp.moneyActivity.getIntFromEditText

class PastaActivity : AppCompatActivity(), TextView.OnEditorActionListener, View.OnClickListener {

    private lateinit var binding: ActivityPastaBinding
    private lateinit var sharedHelperPasta: SharedHelperPasta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPastaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()


        count()
    }

    @SuppressLint("SetTextI18n")
    private fun count() {
        var proportion = 0.0

        if (getIntFromEditText(binding.editTextCooked) != 0 && getIntFromEditText(binding.editTextUncooked) != 0) {
            val cooked = if (binding.checkBoxPan.isChecked) {
                getIntFromEditText(binding.editTextCooked).toDouble() - 672.0
            } else {
                getIntFromEditText(binding.editTextCooked).toDouble()
            }
            val uncooked = getIntFromEditText(binding.editTextUncooked)
            proportion = cooked / uncooked
            binding.textViewProportion.text = "Готовые/сырые: $proportion"

            if (binding.checkBoxPasta.isChecked) {
                val needWater = 0
                binding.textViewProportion.append("\nНужно воды: $needWater")
            } else if (binding.checkBoxRise.isChecked) {
                val needWater = uncooked * 2
                binding.textViewProportion.append("\nНужно воды: $needWater")
            } else if (binding.checkBoxBuckwheat.isChecked) {
                val needWater = uncooked * 1.555
                binding.textViewProportion.append("\nНужно воды: $needWater")
            }
        }

        //Первая порция
        val firstPortion = getIntFromEditText(binding.editTextFirstPortion).toDouble() * proportion
        binding.textViewFirstPortion.text = "$firstPortion"
        //Вторая порция
        val secondPortion = getIntFromEditText(binding.editTextSecondPortion).toDouble() * proportion
        binding.textViewSecondPortion.text = "$secondPortion"
        //Третья порция
        val thirdPortion = getIntFromEditText(binding.editTextThirdPortion).toDouble() * proportion
        binding.textViewThirdPortion.text = "$thirdPortion"
    }

    private fun init() {

        sharedHelperPasta = SharedHelperPasta(binding, this)

        sharedHelperPasta.sharedLoad()

        binding.editTextUncooked.setOnEditorActionListener(this)
        binding.editTextCooked.setOnEditorActionListener(this)
        binding.editTextFirstPortion.setOnEditorActionListener(this)
        binding.editTextSecondPortion.setOnEditorActionListener(this)
        binding.editTextThirdPortion.setOnEditorActionListener(this)

        binding.checkBoxBuckwheat.setOnClickListener(this)
        binding.checkBoxRise.setOnClickListener(this)
        binding.checkBoxPasta.setOnClickListener(this)

        binding.checkBoxPan.setOnCheckedChangeListener { _, _ ->
            count()
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            count()
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        sharedHelperPasta.sharedSave()
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedHelperPasta.sharedSave()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            binding.checkBoxPasta.id -> {
                binding.checkBoxPasta.isChecked = true
                binding.checkBoxRise.isChecked = false
                binding.checkBoxBuckwheat.isChecked = false
                count()
            }

            binding.checkBoxRise.id -> {
                binding.checkBoxRise.isChecked = true
                binding.checkBoxPasta.isChecked = false
                binding.checkBoxBuckwheat.isChecked = false
                count()
            }

            binding.checkBoxBuckwheat.id -> {
                binding.checkBoxBuckwheat.isChecked = true
                binding.checkBoxPasta.isChecked = false
                binding.checkBoxRise.isChecked = false
                count()
            }
        }
    }
}