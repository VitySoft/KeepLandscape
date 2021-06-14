package com.vitysoft.android.keeplandscape

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log


class KeepAliveService : Service() {

    private val receiver = StartLandscapeReceiver()

    private var notification: Notification? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.d("KeepLandscape", "KeepAliveService onCreate")
        registerScreenOn() // 监听亮屏广播
        startForeground()
    }

    private fun registerScreenOn() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(receiver, filter)
    }

    private fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val id = "my_channel_01"
            val name: CharSequence = "default"
            val description = "default"
            val importance = NotificationManager.IMPORTANCE_NONE
            val channel = NotificationChannel(id, name, importance)
            channel.description = description
            channel.enableLights(false)
            channel.lightColor = Color.BLUE
            channel.enableVibration(false)
            channel.vibrationPattern = longArrayOf(100)
            manager.createNotificationChannel(channel)
            val builder = Notification.Builder(this, "my_channel_01")
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Background service running...")
                .setAutoCancel(true)
            notification = builder.build()
        } else {
            notification = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_landscape)
                .setOngoing(true)
                .build()
        }
        startForeground(1, notification)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver);
    }
}
