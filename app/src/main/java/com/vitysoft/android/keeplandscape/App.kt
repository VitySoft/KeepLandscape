package com.vitysoft.android.keeplandscape

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.provider.Settings
import android.util.Log


const val TAG = "KeepLandscape"

const val ACTION_START_LANDSCAPE = "com.vitysoft.android.intent.action.START_LANDSCAPE"
const val ACTION_STOP_LANDSCAPE = "com.vitysoft.android.intent.action.STOP_LANDSCAPE"

fun forceLandscape(context: Context) {
    Log.d(TAG, "forceLandscape")
    val cr = context.contentResolver
    // 禁用自动旋转
    Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION, 0)
    // 设置为横向
    Settings.System.putInt(cr, Settings.System.USER_ROTATION, 1)
}

fun startKeepAliveService(context: Context) {
    Log.d(TAG, "startKeepAliveService")
    val service = Intent(context, KeepAliveService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        context.startForegroundService(service)
    } else {
        context.startService(service)
    }
}

val Any.TAG: String
    get() {
        var name: String
        if (javaClass.isAnonymousClass) {
            name = javaClass.name
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && name.length > 23
            ) {
                // 匿名类取后23个字符
                name = name.substring(name.length - 23, name.length)
            }
        } else {
            name = javaClass.simpleName
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N
                && name.length > 23
            ) {
                // 正常类，取前23个字符
                name = name.substring(0, 23)
            }
        }
        return "KeepLandscape $name"
    }

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}
