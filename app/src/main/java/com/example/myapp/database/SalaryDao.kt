package com.example.myapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SalaryDao {

    @Query("SELECT * FROM SalaryEntity ORDER BY date ASC")
    fun getSalaries(): LiveData<List<SalaryEntity>>

    @Insert
    fun addSalary(salary: SalaryEntity)

    @Update
    fun updateSalary(salary: SalaryEntity)

    @Delete
    fun deleteSalary(salary: SalaryEntity)

}