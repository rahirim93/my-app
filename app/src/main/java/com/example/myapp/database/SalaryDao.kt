package com.example.myapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SalaryDao {

    @Query("SELECT * FROM SalaryEntity ORDER BY date DESC")
    fun getSalaries(): LiveData<List<SalaryEntity>>

    @Query("SELECT * FROM SalaryEntity WHERE id = :id")
    fun getSalaryById(id: String) : LiveData<SalaryEntity?>

    @Insert
    fun addSalary(salary: SalaryEntity)

    @Update
    fun updateSalary(salary: SalaryEntity?)

    @Delete
    fun deleteSalary(salary: SalaryEntity?)

}