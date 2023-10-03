package com.elf.real.me.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.elf.real.me.App.Companion.sharedPrefs
import com.elf.real.me.R
import com.elf.real.me.databinding.ActivityMainBinding
import com.elf.real.me.loading.LoadingActivity
import com.elf.real.me.menu.GameMenuActivity
import com.onesignal.OneSignal
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var vmMain: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(applicationContext).asGif().load(R.drawable.dollar_coin)
            .into(binding.progressBar)

        initOneSignal()

        vmMain = MainViewModel()
        observe()

        if (sharedPrefs.getIsFirstRun()) {
            if (Settings.Secure.getInt(contentResolver, Settings.Global.ADB_ENABLED, 0) == 1) {
                startActivity(GameMenuActivity::class.java)
            } else {
                vmMain.getLink(application)
            }
        } else {
            val link = sharedPrefs.getRef()
            if (link != null) {
                if (link != "") {
                    startActivity(LoadingActivity::class.java)
                } else {
                    startActivity(GameMenuActivity::class.java)
                }
            }
        }
    }

    private fun <T> startActivity(clazz: Class<T>) {
        sharedPrefs.setNotFirstRun()
        startActivity(Intent(this, clazz))
        finish()
    }

    private fun initOneSignal() {
        OneSignal.initWithContext(this@MainActivity)
        OneSignal.setAppId("e619687b-3bd5-45f6-8cea-3d7c7df3792a")
    }

    private fun observe() {
        lifecycleScope.launch {
            vmMain.link.collect {
                if (it != "") {
                    sharedPrefs.setRef(it)
                    startActivity(LoadingActivity::class.java)
                }
            }
        }
        lifecycleScope.launch {
            vmMain.firebaseError.collect {
                if (it == "Error") {
                    startActivity(GameMenuActivity::class.java)
                }
            }
        }
    }
}
