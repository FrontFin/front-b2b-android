package com.getfront.catalog

import com.getfront.catalog.converter.typeOf
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

fun readFile(fileName: String): String {
    val ins = ClassLoader.getSystemClassLoader().getResourceAsStream("access-token.json")
    return BufferedReader(InputStreamReader(ins)).use { it.readText() }
}

internal inline fun <reified T> Gson.from(json: String): T =
    fromJson(json, typeOf<T>())

