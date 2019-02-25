package com.osano.privacymonitor.util

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

fun Context.systemService(serviceName: String) = getSystemService(serviceName)

fun Context.toast(message: String? = null, length: Int = Toast.LENGTH_SHORT) {
    message?.apply {
        Toast.makeText(this@toast, message, length).show()
    }
}

fun Context.hasInternet(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}