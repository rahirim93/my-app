package com.example.myapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SalaryDao {

    @Query("SELECT * FROM SalaryEntity")
    fun getSalaries(): LiveData<List<SalaryEntity>>

    @Insert
    fun addSalary(salary: SalaryEntity)

}