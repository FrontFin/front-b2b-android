package com.getfront.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.getfront.android.databinding.ConnectActivityBinding
import com.getfront.catalog.FrontCatalogContract
import com.getfront.catalog.store.createPreferenceAccountStore
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
        lifecycleScope.launch(Dispatchers.IO) {
            accountStore.accounts().collect { accounts ->
                runOnUiThread {
                    binding.accountsText.text = accounts.joinToString("<br><br>") { account ->
                        """
                           <b>brokerName:</b> ${account.brokerName}<br>
                           <b>accountId:</b> ${account.accountId.take(n = 20)}<br>
                           <b>accessToken:</b> ${account.accessToken.take(n = 20)}...
                           <b>refreshToken:</b> ${account.refreshToken?.take(n = 20)}...
                        """
                    }.let { HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY) }
                }
            }
        }
        /**
         * Launch catalog.
         */
        binding.connectBtn.setOnClickListener {
            catalogResultLauncher.launch(
                "https://front-web-platform-dev.azurewebsites.net:443/b2b-iframe/c48025af-5bbd-424f-b98e-4702c368085f/broker-connect?auth_code=vXELEN6V6QINOjFRN4Z1oTq-vVGXVeumlfckyIhrAKTKGsvavjlyGYYvg_ElbRy58zaXKoEs2fPwUunUOK1zbQ"
            )
        }
    }

    /**
     * Receive connected accounts via ActivityResult.
     */
    private val catalogResultLauncher = registerForActivityResult(
        FrontCatalogContract()
    ) { accounts ->
        if (accounts != null) {
            /**
             * Save accounts to storage.
             */
            lifecycleScope.launch(Dispatchers.IO) {
                accountStore.insert(accounts)
            }
        }
        Log.i("FrontAccounts", accounts.toString())
    }
}
