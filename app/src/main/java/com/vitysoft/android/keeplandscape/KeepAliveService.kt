package com.vitysoft.android.keeplandscape

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import android.view.WindowManager.LayoutParams as WinLayoutParams


const val SERVICE_CHANNEL_ID = "service_channel"

class KeepAliveService : Service() {

    private var running = false
    private val receiver = StartLandscapeReceiver()

    private val windowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

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
            drawOverlay()
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

        startForeground(1, builder.build())
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

    // 画一个不可见的 Overlay，强制横向
    // 空闲休眠后，LineageOS 18.1 上方向会自动变为竖向，导致scrcpy崩溃
    private fun drawOverlay() {
        val view = View(this)
        view.setBackgroundColor(0x00000000)
        val flags = WinLayoutParams.FLAG_NOT_FOCUSABLE or
                WinLayoutParams.FLAG_NOT_TOUCH_MODAL or
                WinLayoutParams.FLAG_LAYOUT_IN_SCREEN
        val params = WinLayoutParams(
            WinLayoutParams.WRAP_CONTENT,
            WinLayoutParams.WRAP_CONTENT,
            getOverlayType(),
            flags,
            PixelFormat.TRANSLUCENT
        )
        params.x = 0
        params.y = 0
        params.width = 0
        params.height = 0
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        windowManager.addView(view, params)
    }

    private fun getOverlayType(): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WinLayoutParams.TYPE_SYSTEM_OVERLAY
        } else {
            WinLayoutParams.TYPE_APPLICATION_OVERLAY
        }
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
    }
}
