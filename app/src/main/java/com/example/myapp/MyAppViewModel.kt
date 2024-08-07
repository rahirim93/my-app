package com.example.myapp

import androidx.lifecycle.ViewModel
import com.example.myapp.database.SalaryEntity

class MyAppViewModel: ViewModel() {

    private val salaryRepository = MyAppRepository.get()

    fun addSalary(salary: SalaryEntity){
        salaryRepository.addSalary(salary)
    }

    fun deleteDalary(salary: SalaryEntity) {
        salaryRepository.deleteSalary(salary)
    }

    val listSalariesLiveData = salaryRepository.getSalaries()
}