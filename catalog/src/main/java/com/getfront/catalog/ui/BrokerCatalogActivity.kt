package com.getfront.catalog.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.getfront.catalog.R
import com.getfront.catalog.databinding.BrokerCatalogActivityBinding
import com.getfront.catalog.entity.CatalogResponse
import com.getfront.catalog.entity.FrontAccount
import com.getfront.catalog.ui.web.BrokerWebChromeClient
import com.getfront.catalog.ui.web.FrontWebViewClient
import com.getfront.catalog.ui.web.JSBridge
import com.getfront.catalog.utils.alertDialog
import com.getfront.catalog.utils.getParcelableList
import com.getfront.catalog.utils.intent
import com.getfront.catalog.utils.lazyNone
import com.getfront.catalog.utils.observeEvent
import com.getfront.catalog.utils.onClick
import com.getfront.catalog.utils.showToast
import com.getfront.catalog.utils.viewBinding
import com.getfront.catalog.utils.viewModel
import java.net.URL

internal class BrokerCatalogActivity : AppCompatActivity() {

    private val link get() = intent.getStringExtra(LINK)!!
    private val linkHost by lazyNone { URL(link).host }
    private var lastUrl = ""

    private val binding by viewBinding(BrokerCatalogActivityBinding::inflate)

    private val viewModel by viewModel<BrokerConnectViewModel>(BrokerConnectViewModel.Factory())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightNavigationBars = true
            isAppearanceLightStatusBars = true
        }

        binding.close.onClick { onClose() }
        binding.toolbar.isVisible = false

        observeCatalogResponse()
        observeThrowable()
        openWebView(link)
    }

    private fun onClose() {
        binding.webView.loadUrl(lastUrl)
    }

    override fun onBackPressed() {
        // binding.webView.evaluateJavascript()
    }

    private fun observeCatalogResponse() {
        observeEvent(viewModel.catalogResponse) {
            onCatalogResponse(it)
        }
    }

    private fun onCatalogResponse(response: CatalogResponse) = when (response) {
        is CatalogResponse.Connected -> onConnected(response)
        is CatalogResponse.Undefined -> Unit
        is CatalogResponse.Close -> finish()
        is CatalogResponse.Done -> finish()
    }

    private fun onConnected(connected: CatalogResponse.Connected) {
        val list = connected.accounts
        val arrayList = if (list is ArrayList<FrontAccount>) list else ArrayList(list)
        val data = Intent().apply { putParcelableArrayListExtra(DATA, arrayList) }
        setResult(RESULT_OK, data)
    }

    private fun observeThrowable() {
        observeEvent(viewModel.throwable) {
            showMessage(it.message)
        }
    }

    private fun showMessage(message: String?) {
        when {
            message.isNullOrEmpty() -> Unit
            message.length <= MAX_TOAST_MSG_LENGTH -> showToast(message)
            else -> alertDialog {
                setMessage(message)
                setPositiveButton(R.string.okay, null)
                setCancelable(false)
                show()
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(url: String) {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportMultipleWindows(true)
            addJavascriptInterface(JSBridge(viewModel), JSBridge.NAME)
            setBackgroundColor(Color.TRANSPARENT)
            webViewClient = WebClient()
            webChromeClient = ChromeClient()
            loadUrl(url)
        }
    }

    inner class WebClient : FrontWebViewClient() {
        override fun onPageCommitVisible(view: WebView?, url: String?) {
            super.onPageCommitVisible(view, url)
            binding.toolbar.isGone = isFrontUrl(url)
        }

        override fun doUpdateVisitedHistory(
            view: WebView?,
            url: String?,
            isReload: Boolean
        ) {
            if (url != null && isFrontUrl(url)) {
                lastUrl = url
            }
            super.doUpdateVisitedHistory(view, url, isReload)
        }

        private fun isFrontUrl(url: String?): Boolean {
            return try {
                URL(url).host == linkHost
            } catch (expected: Exception) {
                false
            }
        }
    }

    inner class ChromeClient : BrokerWebChromeClient() {
        override fun launchWebView(url: String) {
            WebViewActivity.launch(this@BrokerCatalogActivity, url)
        }
    }

    companion object {
        private const val DATA = "data"
        private const val LINK = "link"

        private const val MAX_TOAST_MSG_LENGTH = 38

        fun getIntent(activity: Context, catalogLink: String) =
            intent<BrokerCatalogActivity>(activity)
                .putExtra(LINK, catalogLink)

        fun getAccounts(resultCode: Int, data: Intent?): List<FrontAccount>? {
            return if (resultCode == Activity.RESULT_OK && data != null) {
                getParcelableList(data, DATA)
            } else null
        }
    }
}
