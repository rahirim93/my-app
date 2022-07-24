package com.example.myapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class SalaryEntity(@PrimaryKey var id: UUID = UUID.randomUUID(),
                        var date: Calendar = Calendar.getInstance(),
                        var salary: Int = 0)