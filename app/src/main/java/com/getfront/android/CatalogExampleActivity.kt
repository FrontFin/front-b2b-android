package com.getfront.android

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getfront.android.databinding.ConnectActivityBinding
import com.getfront.catalog.entity.AccessTokenPayload
import com.getfront.catalog.entity.FrontAccount
import com.getfront.catalog.entity.TransferFinishedErrorPayload
import com.getfront.catalog.entity.TransferFinishedPayload
import com.getfront.catalog.entity.TransferFinishedSuccessPayload
import com.getfront.catalog.store.createPreferenceAccountStore
import com.getfront.catalog.store.getAccountsFromPayload
import com.getfront.catalog.ui.FrontCatalogCallback
import com.getfront.catalog.ui.launchCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CatalogExampleActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ConnectActivityBinding.inflate(layoutInflater)
    }
    private val accountStore = createPreferenceAccountStore(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /**
         * Listen for accounts change
         */
        subscribeAccounts()
        /**
         * Launch link
         */
        binding.connectBtn.setOnClickListener {
            launchCatalog(
                this,
                "https://",
                getLinkCallback()
            )
        }
    }

    private fun subscribeAccounts() {
        lifecycleScope.launch(Dispatchers.IO) {
            accountStore.accounts().collect { accounts ->
                displayAccounts(accounts)
            }
        }
    }

    private fun displayAccounts(accounts: List<FrontAccount>) {
        runOnUiThread {
            binding.accountsText.text = toString(accounts)
        }
    }

    private fun getLinkCallback() = object : FrontCatalogCallback {

        override fun onExit() {
            /* no-op */
        }

        override fun onBrokerConnected(payload: AccessTokenPayload) {
            saveAccounts(payload)
        }

        override fun onTransferFinished(payload: TransferFinishedPayload) {
            when (payload) {
                is TransferFinishedErrorPayload -> {
                    showToast("Transfer failed. ${payload.errorMessage}")
                }
                is TransferFinishedSuccessPayload -> {
                    showToast("Transfer succeed")
                }
            }
        }
    }

    private fun saveAccounts(payload: AccessTokenPayload) {
        lifecycleScope.launch(Dispatchers.IO) {
            val accounts = getAccountsFromPayload(payload)
            accountStore.insert(accounts)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
