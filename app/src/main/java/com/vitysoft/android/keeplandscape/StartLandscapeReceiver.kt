package com.vitysoft.android.keeplandscape

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log


const val ACTION_START_LANDSCAPE = "com.vitysoft.android.intent.action.START_LANDSCAPE"
const val ACTION_STOP_LANDSCAPE = "com.vitysoft.android.intent.action.STOP_LANDSCAPE"

class StartLandscapeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_BOOT_COMPLETED != action
            && Intent.ACTION_SCREEN_ON != action
            && ACTION_START_LANDSCAPE != action
        ) {
            Log.d("KeepLandscape", "StartLandscapeReceiver 未知Action: $action")
            return
        }

        Log.d("KeepLandscape", "StartLandscapeReceiver Action: $action")

        //  强制横向
        forceLandscape(context)

        if (Intent.ACTION_BOOT_COMPLETED == action) {
            // 启动保活服务
            startKeepAliveService(context)
        }
    }
}
