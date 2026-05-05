package com.androidforge.habitforge.core.util

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialAdLoading = false

    fun initializeMobileAds() {
        MobileAds.initialize(context) { initializationStatus ->
            val statusMap = initializationStatus.adapterStatusMap
            for ((adapterClassName, status) in statusMap) {
                Timber.d("AdManager: Adapter name: $adapterClassName, Description: ${status.description}, Latency: ${status.latency}, State: ${status.initializationState}")
            }
            Timber.d("AdManager: MobileAds initialized.")
        }
    }

    fun loadInterstitialAd() {
        if (interstitialAd == null && !isInterstitialAdLoading) {
            isInterstitialAdLoading = true
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                Constants.AD_INTERSTITIAL_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Timber.e("AdManager: Interstitial ad failed to load: ${adError.message}")
                        interstitialAd = null
                        isInterstitialAdLoading = false
                    }

                    override fun onAdLoaded(ad: InterstitialAd) {
                        Timber.d("AdManager: Interstitial ad was loaded.")
                        interstitialAd = ad
                        isInterstitialAdLoading = false
                    }
                })
        } else {
            Timber.d("AdManager: Interstitial ad already loaded or loading.")
        }
    }

    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Timber.d("AdManager: Interstitial ad was dismissed.")
                    interstitialAd = null
                    loadInterstitialAd() // Pre-load the next ad
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Timber.e("AdManager: Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitialAd() // Try to load a new one
                    onAdDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("AdManager: Interstitial ad showed on screen.")
                }
            }
            interstitialAd?.show(activity)
        } else {
            Timber.d("AdManager: Interstitial ad wasn't ready yet. Loading one now.")
            loadInterstitialAd() // Try to load it if not ready
            onAdDismissed() // Dismiss immediately if ad not ready to avoid blocking user
        }
    }
}