package com.example.tiempocity.ui.components

import android.annotation.SuppressLint
import android.graphics.Color as AndroidColor
import android.net.http.SslError
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tiempocity.data.model.City

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RainRadarCard(
    city: City,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = "RADAR DE LLUVIA EN DIRECTO (${city.name.uppercase()})",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF546E7A),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162A38)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val radarUrl = "https://embed.windy.com/embed2.html?lat=${city.latitude}&lon=${city.longitude}&detailLat=${city.latitude}&detailLon=${city.longitude}&width=600&height=300&zoom=8&level=surface&overlay=radar&product=radar&menu=&message=&marker=&calendar=now&pressure=&type=map&location=coordinates&detail=&metricWind=default&metricTemp=default&radarRange=-1"

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(AndroidColor.parseColor("#162A38"))
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)

                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClient() {
                            @SuppressLint("WebViewClientOnReceivedSslError")
                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?
                            ) {
                                handler?.proceed()
                            }
                        }

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            allowContentAccess = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            javaScriptCanOpenWindowsAutomatically = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                        }

                        loadUrl(radarUrl)
                    }
                },
                update = { webView ->
                    val newUrl = "https://embed.windy.com/embed2.html?lat=${city.latitude}&lon=${city.longitude}&detailLat=${city.latitude}&detailLon=${city.longitude}&width=600&height=300&zoom=8&level=surface&overlay=radar&product=radar&menu=&message=&marker=&calendar=now&pressure=&type=map&location=coordinates&detail=&metricWind=default&metricTemp=default&radarRange=-1"
                    if (webView.url != newUrl) {
                        webView.loadUrl(newUrl)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}
