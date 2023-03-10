package com.vitysoft.android.keeplandscape

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log


const val TAG = "KeepLandscape"

const val ACTION_START_LANDSCAPE = "com.vitysoft.android.intent.action.START_LANDSCAPE"
const val ACTION_STOP_LANDSCAPE = "com.vitysoft.android.intent.action.STOP_LANDSCAPE"

fun forceLandscape(context: Context) {
    Log.d(TAG, "forceLandscape")
    val cr = context.contentResolver
    // Disable auto rotation
    Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION, 0)
    // Set the screen orientation to landscape
    Settings.System.putInt(cr, Settings.System.USER_ROTATION, 1)
}

fun startKeepAliveService(context: Context) {
    Log.d(TAG, "start KeepAliveService")
    val service = Intent(context, KeepAliveService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        context.startForegroundService(service)
    } else {
        context.startService(service)
    }
}

// Using class name for logging TAG
val Any.TAG: String
    get() {
        var name: String
        if (javaClass.isAnonymousClass) {
            name = javaClass.name
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && name.length > 23
            ) {
                // Using the last 23 characters for anonymous classes
                name = name.substring(name.length - 23, name.length)
            }
        } else {
            name = javaClass.simpleName
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && name.length > 23
            ) {
                // Using the first 23 characters for normal classes
                name = name.substring(0, 23)
            }
        }
        return "KeepLandscape $name"
    }
