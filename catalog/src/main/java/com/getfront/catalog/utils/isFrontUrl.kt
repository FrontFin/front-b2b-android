package com.getfront.catalog.utils

private val frontHosts = arrayOf(
    "https://web.getfront.com",
    "https://sandbox-web.getfront.com",
    "https://front-web-platform-dev.azurewebsites.net"
)

internal fun isFrontUrl(url: String?): Boolean {
    return frontHosts.any { url?.startsWith(it) == true }
}
