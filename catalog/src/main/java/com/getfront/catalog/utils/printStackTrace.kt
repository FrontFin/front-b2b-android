package com.getfront.catalog.utils

import com.getfront.catalog.BuildConfig

fun printStackTrace(e: Throwable) {
    if (BuildConfig.DEBUG) e.printStackTrace()
}
