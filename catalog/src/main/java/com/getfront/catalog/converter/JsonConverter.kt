package com.getfront.catalog.converter

import com.getfront.catalog.entity.TransferFinishedPayload
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

internal object JsonConverter {
    internal fun get(): Gson {
        return GsonBuilder().registerTypeAdapter(
            TransferFinishedPayload::class.java,
            TransferFinishedPayloadDeserializer()
        ).create()
    }
}

internal inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, object : TypeToken<T>() {}.type)

internal inline fun <reified T> JsonDeserializationContext.deserialize(json: JsonElement): T =
    deserialize(json, object : TypeToken<T>() {}.type)
