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
import com.example.myapp.traningTimer.TrainingActivity
import com.google.android.material.slider.Slider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.lifecycle.Observer
import java.io.BufferedReader
import java.io.FileReader
import java.io.InputStream


/** Текущая задача установка будильника с главного экрана
 * 1) Подготовить код для установки будильника
 * * а)
 */

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView

    private lateinit var checkBoxMoney: CheckBox

    private var sharedPreferences: SharedPreferences? = null

    ///// Переменные для будильника
    //Slider для выставления времени будильника
    private lateinit var slider: Slider
    // Alarm manager
    private lateinit var alarmManager: AlarmManager
    // MaterialTimePicker для выбора времени
    private lateinit var materialTimePicker: MaterialTimePicker
    // SimpleDateFormat
    private lateinit var simpleDateFormat: SimpleDateFormat
    private lateinit var button5: Button
    ///// Переменные для будильника

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


        binding.buttonExport.setOnClickListener {
            val file = File(filesDir, "testFile.txt")
            val csvWriter = FileWriter(file, true)
            listSalaries.forEach {
                csvWriter.write("${it.id},${it.date.timeInMillis},${it.salary},${it.expenses}\n")
            }
            csvWriter.close()

            filesDir.listFiles().forEach {
                log("${it.name}")
            }
        }

        binding.buttonImport.setOnClickListener {
            val file = File(filesDir, "testFile.txt")
            val reader = FileReader(file)
            val bufferedReader = BufferedReader(reader)
            //val data = bufferedReader
            try {
                while (true) {
                    var data = bufferedReader.readLine()
                    Log.d("rahirim", data)
                    var dataSplit = data.split(",")

                    var salaryEntity = SalaryEntity()

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dataSplit[1].toLong()
                    salaryEntity.date = calendar

                    salaryEntity.salary = dataSplit[2].toInt()

                    salaryEntity.expenses = dataSplit[3].toIntOrNull()

                    Log.d("rahirim", "${salaryEntity.salary}\t\t${salaryEntity.expenses}")

                    myAppViewModel.addSalary(salaryEntity)

//                    if (data != null) {
//                        var dataSplit = data.split(",")
//                        var salaryEntity = SalaryEntity()
//
//                        val calendar = Calendar.getInstance()
//                        calendar.timeInMillis = dataSplit[1].toLong()
//                        salaryEntity.date = calendar
//
//                        salaryEntity.salary = dataSplit[2].toInt()
//
//
//                        salaryEntity.expenses = dataSplit[3].toInt()
//
//
//                    } else {
//                        break
//                    }
                }
            } catch (e: Exception) {
                e.stackTrace
            }
            bufferedReader.close()

        }



        init()

        val itemList = ArrayList<String>()
        itemList.add("Расходы мои")
        itemList.add("Гречка")
        itemList.add("Макароны")
        itemList.add("Заплата")
        itemList.add("Таймер")

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = adapter
        
        listView.setOnItemClickListener { parent, view, position, id ->
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
                    val intent = Intent(this, TrainingActivity::class.java)
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

        slider = findViewById(R.id.slider)
        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {

            }
        })

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager


        simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        materialTimePicker = MaterialTimePicker.Builder()
            .setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE) + 1)
            .setTitleText("Выберете время для будильника")
            .build()
        materialTimePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.MINUTE, materialTimePicker.minute)
            calendar.set(Calendar.HOUR_OF_DAY, materialTimePicker.hour)

            val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, getAlarmInfoPendingIntent())
            alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent())
            Toast.makeText(this, "Будильник установлен на ${simpleDateFormat.format(calendar.time)}", Toast.LENGTH_SHORT).show()
        }

        button5 = findViewById(R.id.button5)
        button5.setOnClickListener {
            materialTimePicker.show(supportFragmentManager, "datePicker")
        }
    }

    // Функции для будильника
    private fun getAlarmInfoPendingIntent(): PendingIntent {
        val alarmInfoIntent = Intent(this, MainActivity::class.java)
        alarmInfoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        return PendingIntent.getActivity(this, 0, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getAlarmActionPendingIntent(): PendingIntent {
        val intent3 = createIntent("action 1", "extra 1")
        return PendingIntent.getBroadcast(this, 0, intent3, 0)
    }

    private fun createIntent(action: String, extra: String): Intent {
        var intent = Intent(this, Receiver::class.java)
        intent.action = action
        intent.putExtra("extra", extra)
        return intent
    }
    // Функции для будильника
}