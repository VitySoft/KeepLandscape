package com.vitysoft.android.keeplandscape

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

class StartLandscapeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (Intent.ACTION_BOOT_COMPLETED != action
            && Intent.ACTION_SCREEN_ON != action
            && ACTION_START_LANDSCAPE != action
        ) {
            Log.d(TAG, "Unknown action: $action")
            return
        }

        Log.d(TAG, "Action: $action")

        //  强制横向
        forceLandscape(context)

        if (Intent.ACTION_BOOT_COMPLETED == action) {
            // 启动保活服务
            startKeepAliveService(context)
        }
    }
}
