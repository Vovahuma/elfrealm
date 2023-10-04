package com.elf.real.elf.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesUtil(context: Context) {
    private val firstRunKey = "FIRST_RUN_KEY"
    private val firstRefKey = "FIRST_REF_KEY"
    private val refKey = "REF_KEY"
    private val sharedPreference: SharedPreferences =
        context.getSharedPreferences("ELF`S_REALM_PREFERENCE", Context.MODE_PRIVATE)

    fun setNotFirstRun() {
        val editor = sharedPreference.edit()
        editor.putBoolean(firstRunKey, false)
        editor.apply()
    }

    fun getIsFirstRun(): Boolean {
        return sharedPreference.getBoolean(firstRunKey, true)
    }

    fun setRefNotFirst() {
        val editor = sharedPreference.edit()
        editor.putBoolean(firstRefKey, false)
        editor.apply()
    }

    fun getRefFirst(): Boolean {
        return sharedPreference.getBoolean(firstRefKey, true)
    }

    fun getRef(): String? {
        return sharedPreference.getString(refKey, "")
    }

    fun setRef(ref: String) {
        val editor = sharedPreference.edit()
        editor.putString(refKey, ref)
        editor.apply()
    }

    fun deleteRef() {
        val editor = sharedPreference.edit()
        editor.remove(refKey)
        editor.apply()
    }
}