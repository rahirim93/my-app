package com.example.myapp

import androidx.lifecycle.ViewModel
import com.example.myapp.database.SalaryEntity

class SalaryViewModel: ViewModel() {

    private val salaryRepository = MyAppRepository.get()

    fun updateSalary(salary: SalaryEntity) {
        salaryRepository.updateSalary(salary)
    }

    fun deleteSalary(salary: SalaryEntity) {
        salaryRepository.deleteSalary(salary)
    }


}