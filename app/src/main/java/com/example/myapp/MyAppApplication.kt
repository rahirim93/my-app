package com.example.myapp

import android.app.Application

class MyAppApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        MyAppRepository.initialize(this)
    }
}