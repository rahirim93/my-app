package com.example.myapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ SalaryEntity::class ], version = 1)
@TypeConverters(SalaryTypeConverters::class)
abstract class SalaryDatabase: RoomDatabase() {

    abstract fun salaryDao(): SalaryDao

}