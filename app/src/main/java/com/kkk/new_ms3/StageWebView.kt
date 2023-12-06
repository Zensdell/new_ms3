package com.kkk.new_ms3

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView

class StageWebView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stage_web_view)
        var stageUrl = intent.getStringExtra("stageUrl")
        var singerId = intent.getStringExtra("singerId")

        val stageWebView = findViewById<WebView>(R.id.stage_webview)
        stageWebView.webViewClient = WebViewClient()
        stageWebView.webChromeClient = MyWebClient()
        if (stageUrl != null) {
            stageWebView.settings.domStorageEnabled = true
            stageWebView.settings.javaScriptEnabled = true
            stageWebView.settings.useWideViewPort = true
            stageWebView.settings.supportZoom()
            stageWebView.settings.supportMultipleWindows()
            stageWebView.loadUrl(stageUrl)
        }

        val ytName = findViewById<TextView>(R.id.yt_namelist)
        ytName.text = "미스트롯3 $singerId 무대영상"


    }

    inner class MyWebClient : WebChromeClient() {

        private var mCustomView: View? = null
        private var mCustomViewCallback: WebChromeClient.CustomViewCallback? = null
        protected var mFullscreenContainer: FrameLayout? = null
        private var mOriginalOrientation: Int = 0
        private var mOriginalSystemUiVisibility: Int = 0

        override fun getDefaultVideoPoster(): Bitmap? {
            return if (this == null) {
                null
            } else BitmapFactory.decodeResource(resources, 2130837573)
        }

        override fun onHideCustomView() {
            (window.decorView as FrameLayout).removeView(this.mCustomView)
            this.mCustomView = null
            window.decorView.systemUiVisibility = this.mOriginalSystemUiVisibility
            requestedOrientation = this.mOriginalOrientation
            this.mCustomViewCallback!!.onCustomViewHidden()
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            this.mCustomViewCallback = null
        }

        override fun onShowCustomView(
            paramView: View, paramCustomViewCallback:
            WebChromeClient.CustomViewCallback
        ) {
            if (this.mCustomView != null) {
                onHideCustomView()
                return
            }
            this.mCustomView = paramView
            this.mOriginalSystemUiVisibility = window.decorView.systemUiVisibility
            this.mOriginalOrientation = requestedOrientation
            this.mCustomViewCallback = paramCustomViewCallback
            (window.decorView as FrameLayout).addView(
                this.mCustomView,
                FrameLayout.LayoutParams(-1, -1)
            )
            window.decorView.systemUiVisibility = 3846
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

    }
}