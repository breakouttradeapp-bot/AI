package com.aikundli

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KundliApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize AdMob on background thread (Google-recommended)
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@KundliApplication) { initStatus ->
                val statusMap = initStatus.adapterStatusMap
                for ((adapter, status) in statusMap) {
                    android.util.Log.d("AdMob", "Adapter: $adapter, Status: ${status.initializationState}")
                }
            }
        }

        // Set test device IDs during development
        // REMOVE or replace before release
        val testDeviceIds = listOf("YOUR_TEST_DEVICE_ID")
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)
    }
}
