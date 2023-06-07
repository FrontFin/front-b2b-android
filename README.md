## Front Finance Android SDK

Let your users connect brokerage accounts via Front Android SDK.

### Installation

Add `catalog` dependency to your `build.gradle`.
```gradle
dependencies {
    implementation 'com.getfront:catalog:1.0.0-beta03'
}
```

### Launch Catalog

Use `catalogLink` to connect a brokerage account or initiate a crypto transfer.
```kotlin
import com.getfront.catalog.entity.AccessTokenPayload
import com.getfront.catalog.entity.TransferFinishedErrorPayload
import com.getfront.catalog.entity.TransferFinishedPayload
import com.getfront.catalog.entity.TransferFinishedSuccessPayload
import com.getfront.catalog.ui.FrontCatalogCallback
import com.getfront.catalog.ui.launchCatalog

class CatalogExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        connectBtn.setOnClickListener {
            launchCatalog(
                this,
                "catalogLink",
                getCatalogCallback()
            )
        }
    }

    private fun getCatalogCallback() = object : FrontCatalogCallback {

        override fun onExit() {
            Log.d("FrontCatalog", "Catalog closed")
        }

        override fun onBrokerConnected(payload: AccessTokenPayload) {
            Log.d("FrontCatalog", "Broker connected. $payload")
        }

        override fun onTransferFinished(payload: TransferFinishedPayload) {
            when (payload) {
                is TransferFinishedSuccessPayload -> {
                    Log.d("FrontCatalog", "Transfer succeed. $payload")
                }
                is TransferFinishedErrorPayload -> {
                    Log.d("FrontCatalog", "Transfer failed. $payload")
                }
            }
        }
    }
}
```

### Keep accounts in a safe place

You may keep accounts in built-in encrypted storage.
```kotlin
import com.getfront.catalog.store.createPreferenceAccountStore
import com.getfront.catalog.store.getAccountsFromPayload

// Get instance
private val accountStore: FrontAccountStore = createPreferenceAccountStore(context)

// Subscribe for accounts
lifecycleScope.launch(Dispatchers.IO) {
    accountStore.accounts().collect { accounts ->
        Log.d("FrontCatalog", "Accounts: $accounts")
    }
}

// Save accounts
lifecycleScope.launch(Dispatchers.IO) {
    val accounts = getAccountsFromPayload(/* AccessTokenPayload */ payload)
    accountStore.insert(accounts)
}
```
