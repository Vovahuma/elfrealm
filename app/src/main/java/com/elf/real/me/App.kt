package com.elf.real.me

import android.app.Application
import com.elf.real.me.util.SharedPreferencesUtil

class App : Application() {

    companion object {
        lateinit var sharedPrefs: SharedPreferencesUtil
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPreferencesUtil(applicationContext)
    }
}