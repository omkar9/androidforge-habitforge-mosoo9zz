package com.androidforge.habitforge.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.androidforge.habitforge.R
import com.androidforge.habitforge.core.util.Constants
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import timber.log.Timber

@Composable
fun AdBannerComposable(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val screenWidth = LocalConfiguration.current.screenWidthDp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AdSize.BANNER.height.dp) // Standard banner height
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(ctx, screenWidth))
                    adUnitId = Constants.AD_BANNER_UNIT_ID
                    loadAd(AdRequest.Builder().build())
                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            Timber.d("AdBannerComposable: Ad loaded.")
                        }
                        override fun onAdFailedToLoad(adError: com.google.android.gms.ads.LoadAdError) {
                            Timber.e("AdBannerComposable: Ad failed to load: ${adError.message}")
                        }
                        override fun onAdOpened() { Timber.d("AdBannerComposable: Ad opened.") }
                        override fun onAdClicked() { Timber.d("AdBannerComposable: Ad clicked.") }
                        override fun onAdClosed() { Timber.d("AdBannerComposable: Ad closed.") }
                    }
                }
            },
            update = { adView ->
                // Optional: Update logic if needed, e.g., re-load ad on configuration change
            }
        )
    }
}