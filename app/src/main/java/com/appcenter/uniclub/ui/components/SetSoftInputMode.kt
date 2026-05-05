package com.appcenter.uniclub.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun SetSoftInputMode(mode: Int) {
    val activity = LocalContext.current.findActivity()

    DisposableEffect(activity, mode) {
        val window = activity?.window
        val oldMode = window?.attributes?.softInputMode

        window?.setSoftInputMode(mode)

        onDispose {
            if (oldMode != null) {
                window.setSoftInputMode(oldMode)
            }
        }
    }
}