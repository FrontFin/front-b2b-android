package com.getfront.catalog.utils

import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat

internal fun Activity.windowInsetsController(controller: WindowInsetsControllerCompat.() -> Unit) {
    controller(WindowInsetsControllerCompat(window, window.decorView))
}
