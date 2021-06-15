package com.vitysoft.android.keeplandscape

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

const val SERVICE_CHANNEL_ID = "service_channel"

class KeepAliveService : Service() {

    private var running = false
    private val receiver = StartLandscapeReceiver()

    private var notification: Notification? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if (intent != null && ACTION_STOP_LANDSCAPE == intent.action) {
            Log.d(TAG, "stop service")
            stopSelf()
            return START_NOT_STICKY
        }
        if (!running) {
            Log.d(TAG, "start service")
            running = true
            registerScreenOn() // 监听亮屏广播
            startForeground()
        }
        return START_STICKY
    }

    private fun registerScreenOn() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(receiver, filter)
    }

    private fun startForeground() {
        createNotificationChannel()
        val intent = Intent(this, KeepAliveService::class.java)
        intent.action = ACTION_STOP_LANDSCAPE
        val pendingIntent: PendingIntent = PendingIntent.getService(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_landscape)
            .setContentText(getString(R.string.service_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notification = builder.build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.service_channel_name)
            val descriptionText = getString(R.string.service_channel_description)
            val importance = NotificationManager.IMPORTANCE_NONE
            val channel = NotificationChannel(SERVICE_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
    }
}
