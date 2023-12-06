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

class VoteWebView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vote_web_view)

        val vtWebView = findViewById<WebView>(R.id.voteWeb)
        vtWebView.webViewClient= WebViewClient()
        vtWebView.loadUrl("https://1fa.societyfa.com/entry/%EB%AF%B8%EC%8A%A4%ED%8A%B8%EB%A1%AF3-%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD")

        if (vtWebView != null) {
            vtWebView.getSettings().setDomStorageEnabled(true)
            vtWebView.getSettings().setJavaScriptEnabled(true)
            vtWebView.loadUrl("https://1fa.societyfa.com/entry/%EB%AF%B8%EC%8A%A4%ED%8A%B8%EB%A1%AF3-%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD")

        }

        vtWebView.webChromeClient = MyWebClient()

    }
    override fun onPause(){
        val vtWebView = findViewById<WebView>(R.id.voteWeb)
        super.onPause()
        if(vtWebView!=null) {
            vtWebView.onPause()
            vtWebView.pauseTimers()
        }
    }
    override fun onResume(){
        val vtWebView = findViewById<WebView>(R.id.voteWeb)
        super.onResume()
        if(vtWebView!=null){
            vtWebView.onResume()
            vtWebView.resumeTimers()
        }
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