package com.example.myapp.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//@Database(entities = [ SalaryEntity::class ], version = 1)
//@TypeConverters(SalaryTypeConverters::class)
//abstract class SalaryDatabase: RoomDatabase() {
//    abstract fun salaryDao(): SalaryDao
//}

@Database(entities = [ SalaryEntity::class ], version = 2,
autoMigrations = [
    AutoMigration(from = 1, to = 2)
])
@TypeConverters(SalaryTypeConverters::class)
abstract class SalaryDatabase: RoomDatabase() {
    abstract fun salaryDao(): SalaryDao
}