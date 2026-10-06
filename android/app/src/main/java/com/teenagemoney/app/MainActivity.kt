package com.teenagemoney.app

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowCompat
import androidx.webkit.WebViewAssetLoader

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Styling status bar and navigation bar to match Teenage Money brand
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = Color.parseColor("#292834")
        window.navigationBarColor = Color.parseColor("#FFFFFF")
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        insetsController.isAppearanceLightNavigationBars = true

        webView = WebView(this)
        setContentView(webView)

        // Configure WebViewAssetLoader for offline assets from assets folder
        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.allowFileAccess = false
        settings.allowContentAccess = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.displayZoomControls = false
        settings.builtInZoomControls = false
        settings.setSupportZoom(false)

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.setBackgroundColor(Color.parseColor("#FBFAF8"))

        // Register Native JavaScript Interface
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                val url = request.url
                // Let asset loader intercept secure internal assets domain
                val response = assetLoader.shouldInterceptRequest(url)
                if (response != null) return response

                // Direct asset loading for index.html at root
                if (url.host == "appassets.androidplatform.net" && (url.path == "/" || url.path == "/index.html")) {
                    return try {
                        val stream = assets.open("index.html")
                        WebResourceResponse("text/html", "UTF-8", stream)
                    } catch (_: Exception) {
                        null
                    }
                }
                return null
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Handle back press within web history
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // Load the bundled entry point with a stable local origin. This avoids the
        // blank-screen failure caused by relying on a root URL interception for
        // index.html while keeping relative JS/CSS asset paths offline.
        val entryHtml = assets.open("index.html").bufferedReader(Charsets.UTF_8).use { it.readText() }
        webView.loadDataWithBaseURL(
            "https://appassets.androidplatform.net/",
            entryHtml,
            "text/html",
            "UTF-8",
            null
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }

    class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun vibrate(milliseconds: Long) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator?.vibrate(
                        VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(milliseconds)
                    }
                }
            } catch (_: Exception) {}
        }

        @JavascriptInterface
        fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
