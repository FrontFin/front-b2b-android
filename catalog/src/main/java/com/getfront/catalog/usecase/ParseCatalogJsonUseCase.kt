package com.getfront.catalog.usecase

import com.getfront.catalog.converter.JsonConverter
import com.getfront.catalog.entity.AccessTokenPayload
import com.getfront.catalog.entity.AccessTokenResponse
import com.getfront.catalog.entity.FrontAccount
import com.getfront.catalog.entity.JsError
import com.getfront.catalog.entity.JsType
import com.getfront.catalog.entity.LinkEvent
import com.getfront.catalog.entity.TransferFinishedResponse
import com.getfront.catalog.entity.Type
import com.getfront.catalog.utils.printStackTrace
import com.getfront.catalog.utils.runCatching
import kotlinx.coroutines.CoroutineDispatcher

internal class ParseCatalogJsonUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val converter: JsonConverter,
) {
    suspend fun launch(json: String) = runCatching(dispatcher) {
        val event = converter.parse<JsType>(json)

        when (event.type) {
            Type.done -> LinkEvent.Done
            Type.close -> LinkEvent.Close
            Type.showClose -> LinkEvent.ShowClose
            Type.brokerageAccountAccessToken -> {
                try {
                    val payload = converter.parse<AccessTokenResponse>(json).payload
                    LinkEvent.Payload(payload)
                } catch (e: Exception) {
                    printStackTrace(e)
                    error("Faced an error while parsing access token payload: ${e.message}")
                }
            }
            Type.transferFinished -> {
                try {
                    val payload = converter.parse<TransferFinishedResponse>(json).payload
                    LinkEvent.Payload(payload)
                } catch (e: Exception) {
                    printStackTrace(e)
                    error("Faced an error while parsing transfer finished payload: ${e.message}")
                }
            }
            Type.error -> {
                val response = converter.parse<JsError>(json)
                error(response.errorMessage ?: "Undefined error")
            }
            else -> LinkEvent.Undefined
        }
    }

    private fun mapAccounts(payload: AccessTokenPayload): List<FrontAccount> {
        return payload.accountTokens.map {
            FrontAccount(
                accessToken = it.accessToken,
                refreshToken = it.refreshToken,
                accountId = it.account.accountId,
                accountName = it.account.accountName,
                brokerType = payload.brokerType,
                brokerName = payload.brokerName
            )
        }
    }
}
