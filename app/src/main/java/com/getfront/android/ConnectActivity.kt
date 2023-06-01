package com.getfront.android

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.getfront.android.databinding.ConnectActivityBinding
import com.getfront.catalog.FrontCatalogContract
import com.getfront.catalog.entity.AccessTokenPayload
import com.getfront.catalog.entity.FrontAccount
import com.getfront.catalog.entity.TransferFinishedErrorPayload
import com.getfront.catalog.entity.TransferFinishedSuccessPayload
import com.getfront.catalog.store.createPreferenceAccountStore
import com.getfront.catalog.store.getAccountsFromPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConnectActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ConnectActivityBinding.inflate(layoutInflater)
    }
    private val accountStore = createPreferenceAccountStore(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        /**
         * Listen for accounts change.
         */
        subscribeAccounts()
        /**
         * Launch catalog.
         */
        binding.connectBtn.setOnClickListener {
            catalogResultLauncher.launch(
                "catalogLink"
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

    /**
     * Receive result payload via ActivityResult.
     */
    private val catalogResultLauncher = registerForActivityResult(
        FrontCatalogContract()
    ) { payload ->
        when (payload) {
            is AccessTokenPayload -> onAccessTokenReceived(payload)
            is TransferFinishedSuccessPayload -> onTransferSucceed(payload)
            is TransferFinishedErrorPayload -> onTransferFailed(payload)
            null -> Unit /* no-op */
        }
    }

    private fun onAccessTokenReceived(payload: AccessTokenPayload) {
        Toast.makeText(this, "Account connected!", Toast.LENGTH_SHORT).show()
        lifecycleScope.launch(Dispatchers.IO) {
            val accounts = getAccountsFromPayload(payload)
            accountStore.insert(accounts)
        }
    }

    private fun onTransferSucceed(payload: TransferFinishedSuccessPayload) {
        Toast.makeText(this, "Transfer succeed: ${payload.txId}", Toast.LENGTH_SHORT).show()
    }

    private fun onTransferFailed(payload: TransferFinishedErrorPayload) {
        Toast.makeText(this, "Transfer failed: ${payload.errorMessage}", Toast.LENGTH_LONG).show()
    }
}
