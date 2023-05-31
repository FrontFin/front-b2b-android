package com.getfront.catalog

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.getfront.catalog.entity.FrontAccount
import com.getfront.catalog.entity.FrontPayload
import com.getfront.catalog.ui.BrokerCatalogActivity

/**
 * Implements [ActivityResultContract] to start catalog activity
 * and extract list of [FrontAccount]s.
 * ```
 *  private val catalogResultLauncher = registerForActivityResult(
 *      FrontCatalogContract()
 *  ) {
 *      Log.i("ConnectedAccounts", it.toString())
 *  }
 *
 *  connectBtn.setOnClickListener {
 *      catalogResultLauncher.launch("catalogLink")
 *  }
 * ```
 */
@Suppress("unused")
class FrontCatalogContract : ActivityResultContract<String, FrontPayload?>() {

    /**
     * Returns intent to start catalog activity.
     */
    override fun createIntent(context: Context, input: String): Intent {
        return BrokerCatalogActivity.getIntent(context, input)
    }

    /**
     * Extracts list of [FrontAccount]s from activity result intent.
     */
    override fun parseResult(resultCode: Int, intent: Intent?): FrontPayload? {
        return BrokerCatalogActivity.getPayload(resultCode, intent)
    }
}
