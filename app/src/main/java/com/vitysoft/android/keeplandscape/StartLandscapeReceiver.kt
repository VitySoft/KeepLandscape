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

        // Write system settings ACCELEROMETER_ROTATION and USER_ROTATION
        // to keep system dialogs on landscape
        // Especially needed for ACTION_SCREEN_ON
        forceLandscape(context)

        // Start KeepAliveService on ACTION_BOOT_COMPLETED
        if (Intent.ACTION_BOOT_COMPLETED == action) {
            startKeepAliveService(context)
        }
    }
}
