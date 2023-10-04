package com.elf.real.elf

import android.app.Application
import com.elf.real.elf.util.SharedPreferencesUtil

class App : Application() {

    companion object {
        lateinit var sharedPrefs: SharedPreferencesUtil
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPreferencesUtil(applicationContext)
    }
}