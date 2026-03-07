package com.aikundli.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

// ── Banner Ad Composable ───────────────────────────────────────────────────

@Composable
fun BannerAdView(adUnitId: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory  = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}

// ── Rewarded Ad Manager ───────────────────────────────────────────────────

object RewardedAdManager {

    private var rewardedAd: RewardedAd? = null
    private var isLoading  = false

    // Test rewarded ad unit ID — replace with real before release
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    fun loadAd(context: Context, onLoaded: () -> Unit = {}, onFailed: () -> Unit = {}) {
        if (isLoading || rewardedAd != null) return
        isLoading = true
        RewardedAd.load(
            context,
            REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading  = false
                    onLoaded()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isLoading  = false
                    onFailed()
                }
            }
        )
    }

    fun showAd(
        context: Context,
        onRewarded: () -> Unit,
        onDismissed: () -> Unit = {},
        onNotAvailable: () -> Unit = {}
    ) {
        val activity = context as? android.app.Activity
        if (activity == null || rewardedAd == null) {
            onNotAvailable()
            return
        }

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                onDismissed()
                // Pre-load next ad
                loadAd(context)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                rewardedAd = null
                onNotAvailable()
            }
        }

        rewardedAd?.show(activity) { rewardItem ->
            // User earned reward
            onRewarded()
        }
    }

    fun isAdReady() = rewardedAd != null
}
