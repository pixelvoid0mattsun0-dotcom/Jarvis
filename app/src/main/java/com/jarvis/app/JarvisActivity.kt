package com.jarvis.app
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.*

class JarvisActivity : Activity() {
    private lateinit var w: WebView
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 2)
        w = WebView(this)
        w.settings.javaScriptEnabled = true
        w.settings.domStorageEnabled = true
        w.settings.mediaPlaybackRequiresUserGesture = false
        w.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(r: PermissionRequest) { r.grant(r.resources) }
        }
        w.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(v: WebView, r: WebResourceRequest): Boolean {
                val u = r.url.toString()
                if (u.startsWith("file:")) return false
                try {
                    startActivity(if (u.startsWith("intent:")) Intent.parseUri(u, Intent.URI_INTENT_SCHEME)
                                  else Intent(Intent.ACTION_VIEW, Uri.parse(u)))
                } catch (e: Exception) {}
                return true
            }
        }
        setContentView(w)
        w.loadUrl("file:///android_asset/jarvis.html")
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { if (w.canGoBack()) w.goBack() else super.onBackPressed() }
}
