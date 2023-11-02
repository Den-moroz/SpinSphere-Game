package core.basesyntax.whiteball.presentation.ui

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private val URL = "https://www.google.com"

@Composable
fun WebViewComponent() {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                }
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()
                loadUrl(URL)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
