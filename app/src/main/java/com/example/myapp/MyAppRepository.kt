package com.example.myapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.myapp.database.SalaryDatabase
import com.example.myapp.database.SalaryEntity
import java.lang.IllegalStateException
import java.util.concurrent.Executors

private const val DATABASE_NAME = "myApp_database"

class MyAppRepository private constructor(context: Context){

    private val database : SalaryDatabase = Room.databaseBuilder(
        context.applicationContext,
        SalaryDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val salaryDao = database.salaryDao()

    private val executor = Executors.newSingleThreadExecutor()

    fun getSalaries(): LiveData<List<SalaryEntity>> = salaryDao.getSalaries()

    fun addSalary(salary: SalaryEntity) {
        executor.execute {
            salaryDao.addSalary(salary)
        }
    }

    companion object {
        private var INSTANCE: MyAppRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = MyAppRepository(context)
            }
        }

        fun get(): MyAppRepository {
            return INSTANCE ?:
            throw IllegalStateException("BatteryRepository must be initialized")
        }
    }

}