package com.getfront.catalog.ui

import android.util.Log
import android.webkit.JavascriptInterface

internal class JSBridge(private val callback: Callback) {

    @JavascriptInterface
    fun sendNativeMessage(payloadJson: String) {
        Log.d("frontLog", "sendNativeMessage: $payloadJson")
        callback.onJsonReceived(payloadJson)
    }

    companion object {
        const val NAME = "JSBridge"
    }

    interface Callback {
        fun onJsonReceived(payloadJson: String)
    }
}
