package com.example.myapp.settingsActivity

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.myapp.MyAppViewModel
import com.example.myapp.R
import com.example.myapp.database.SalaryEntity
import com.example.myapp.databinding.ActivitySettingsBinding
import com.example.myapp.log
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.Calendar

private const val FILE_NAME = "testFile.txt"

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val myAppViewModel: MyAppViewModel by viewModels()

    private var listSalaries: List<SalaryEntity>? = null

    private lateinit var getFile: ActivityResultLauncher<String>

    private var listForDB: ArrayList<SalaryEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        /** План добавления экспорта данных БД перед миграцией.
         * 1. Формирование файла с данными.
         *      - Проверяем есть ли сейчас такой файл.
         *      - Сформировать файл локально.
         *      - Сохраняем локально.
         *      - Занести функцию в базу с примерами.
         * 2. Отправка на яндекс диск (не обязательно).
         * 3. Получение с яндекс диска (не обязательно).
         * 4.
         * 4. Отправка в лог для проверки.
         * 5. Импорт в базу данных.
         *
         * Данное активити всегда должно понимать есть ли в текущий момомент созданный файл и отображать это.
         *
         * Сделать функцию просмотра содержимого файла
         *
         * Поискать можно ли как то вытащить файл из яндекс диска
         *
         * Подумать о том как сделать автоматическое формирование строоки с терминаторами
         * получая на входе сущность, а на выходе строку для добавления в файл. Тогда
         * функция будет универсальной. А пока нужно формировать строку вручную учитывая поля сущности.
         */


        initObservers()

        initButtons()

        getFile = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback {
            if (it != null) {
                createFileFromUri(it)
            }
            updateFileState()
        })
    }

    private fun initButtons() {
        binding.buttonCreate.setOnClickListener {
            listSalaries?.let { createFile(it) }
            updateFileState()
        }

        binding.buttonDelete.setOnClickListener {
            deleteFile(File(filesDir, FILE_NAME))
            updateFileState()
        }

        binding.buttonPrint.setOnClickListener {
            printFileLog(File(filesDir, FILE_NAME))
        }

        binding.buttonShare.setOnClickListener {
            shareFile(File(filesDir, FILE_NAME))
        }

        binding.buttonCreateUri.setOnClickListener {
            getFile.launch("*/*")
        }

        binding.buttonOpen.setOnClickListener {
            openFile(File(filesDir, FILE_NAME))
        }

        binding.buttonCreateList.setOnClickListener {
            createListForDB(File(filesDir, FILE_NAME))
            listForDB?.forEach {
                log("${it.date.timeInMillis}\t${it.salary}\t${it.expenses}")
            }
        }

        binding.buttonImportList.setOnClickListener {
            listForDB?.let { it1 -> importToDB(it1) }
        }

        binding.buttonDeleteDB.setOnClickListener {
            listSalaries?.forEach {
                myAppViewModel.deleteDalary(it)
            }
        }
    }

    private fun initObservers() {
        val salaryObserver = Observer<List<SalaryEntity>>{salaries ->
            listSalaries = salaries
        }
        myAppViewModel.listSalariesLiveData.observe(this, salaryObserver)
    }

    //Функция формирования файла с данными с БД
    private fun createFile(list: List<SalaryEntity>) {
        val file = File(filesDir, FILE_NAME)
        val writer = FileWriter(file, true)
        list.forEach {
            writer.write("${it.id},${it.date.timeInMillis},${it.salary},${it.expenses}\n")
        }
        writer.close()
    }

    // Получение файла из файл эксплорера
    private fun createFileFromUri(uri: Uri) {
        val file = File(filesDir, FILE_NAME)
        val writer = FileWriter(file, true)

        var contentResolver = applicationContext.contentResolver
        val  inputStream = contentResolver.openInputStream(uri)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        try {
            while (true) {
                var data = bufferedReader.readLine()
                if (data != null) {
                    log(data)
                    writer.write(data + "\n")
                } else {
                    break
                }
            }
        } catch (e: java.lang.Exception) {
            Log.d("csv", "Exception")
            Log.d("csv", "${e.printStackTrace()}")
        }
        bufferedReader.close()
        inputStream?.close()
        writer.close()
    }

    // Удаление файла
    private fun deleteFile(file: File) {
        file.delete()
    }

    override fun onResume() {
        super.onResume()
        updateFileState()
    }

    // Обновление состояния файла
    private fun updateFileState() {
        filesDir.listFiles()?.forEach {
            //log("${it.name}")
            if (it.name == FILE_NAME) {
                //log("такой файл есть")
                binding.buttonOpen.isEnabled = true
                binding.buttonCreateUri.isEnabled = false
                binding.buttonShare.isEnabled = true
                binding.buttonPrint.isEnabled = true
                binding.buttonCreate.isEnabled = false
                binding.buttonDelete.isEnabled = true
                binding.buttonState.setBackgroundColor(Color.GREEN)
            } else {
                binding.buttonOpen.isEnabled = false
                binding.buttonCreateUri.isEnabled = true
                binding.buttonShare.isEnabled = false
                binding.buttonPrint.isEnabled = false
                binding.buttonCreate.isEnabled = true
                binding.buttonDelete.isEnabled = false
                binding.buttonState.setBackgroundColor(Color.RED)
            }
        }
    }

    // Вывод содержимого файла в лог
    private fun printFileLog(file: File) {
        log("")
        log("start")
        var counter = 1
        val reader = FileReader(file)
        val bufferedReader = BufferedReader(reader)
        try {
            while (true) {
                var data = bufferedReader.readLine()
                if (data != null) {
                    log("$counter " + data)
                    counter = counter + 1
                } else {
                    log("finish")
                    log("")
                    break
                }
            }
        } catch (e: Exception) {
            e.stackTrace
        }
        bufferedReader.close()
    }

    // Поделится файлом
    private fun shareFile(file: File) {
        val fileUri: Uri? = try {
            FileProvider.getUriForFile(
                this@SettingsActivity,
                "com.example.myapp.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, fileUri)
        intent.type = "text/plain"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(Intent.createChooser(intent, "Share file:"))
    }

    // Открыть файл
    private fun openFile(file: File) {
        val fileUri: Uri? = try {
            FileProvider.getUriForFile(this@SettingsActivity, "com.example.myapp.fileprovider", file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        var intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.setDataAndType(fileUri, "text/plain")
        startActivity(intent)
    }

    // Создать набор сущностей для импорта в БД
    private fun createListForDB(file: File) {
        listForDB = ArrayList<SalaryEntity>() // Инициализируем пустым списком

        val reader = FileReader(file)
        val bufferedReader = BufferedReader(reader)
        try {
            while (true) {
                val data = bufferedReader.readLine() // Читаем строчку
                if (data != null) { // Если строчка не null
                    val dataSplit = data.split(",") // Разделяем строку (терминатором в данном случае является запятая)

                    var salaryEntity = SalaryEntity() // Создаем пустой объект сущности

                    // Заполняем поле времени
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dataSplit[1].toLong()
                    salaryEntity.date = calendar

                    salaryEntity.salary = dataSplit[2].toInt()

                    salaryEntity.expenses = dataSplit[3].toIntOrNull()

                    listForDB!!.add(salaryEntity)
                } else {
                    break
                }
            }

        } catch (e: Exception) {
            e.stackTrace
        }
        reader.close()
        bufferedReader.close()
    }

    private fun importToDB(arrayList: ArrayList<SalaryEntity>) {
        arrayList.forEach {
            myAppViewModel.addSalary(it)
        }
    }
}