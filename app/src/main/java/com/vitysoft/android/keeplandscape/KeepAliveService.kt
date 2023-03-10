package com.vitysoft.android.keeplandscape

import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import android.view.WindowManager.LayoutParams as WinLayoutParams


const val SERVICE_CHANNEL_ID = "service_channel"

class KeepAliveService : Service() {

    private var running = false
    private val receiver = StartLandscapeReceiver()
    private var overlay: View? = null

    private val windowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if (intent != null && ACTION_STOP_LANDSCAPE == intent.action) {
            stopLandscape()
            return START_NOT_STICKY
        }
        if (!running) {
            running = true
            startLandscape()
        }
        return START_STICKY
    }

    private fun startLandscape() {
        Log.d(TAG, "start service")
        registerScreenOn() // 监听亮屏广播
        drawOverlay()
        startForeground()
    }

    private fun stopLandscape() {
        Log.d(TAG, "stop service")

        // 恢复自动旋转
        Settings.System.putInt(
            contentResolver,
            Settings.System.ACCELEROMETER_ROTATION, 1
        )

        unregisterScreenOn()
        removeOverlay()
        stopSelf()
    }

    private fun registerScreenOn() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        registerReceiver(receiver, filter)
    }

    private fun unregisterScreenOn() {
        unregisterReceiver(receiver)
    }

    private fun startForeground() {

        createNotificationChannel()

        val intent = Intent(this, KeepAliveService::class.java)
        intent.action = ACTION_STOP_LANDSCAPE
        val pendingIntent: PendingIntent = PendingIntent.getService(this, 0, intent, FLAG_IMMUTABLE)
        val icon = IconCompat.createWithResource(this, android.R.drawable.ic_delete)
        val action = NotificationCompat.Action.Builder(icon, "Stop", pendingIntent).build()

        val builder = NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_landscape)
            .setContentText(getString(R.string.service_description))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(action)
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
        if (overlay != null) {
            removeOverlay()
        }
        overlay = View(this)
        overlay!!.setBackgroundColor(0x00000000)
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
        windowManager.addView(overlay, params)
    }

    private fun removeOverlay() {
        if (overlay != null) {
            windowManager.removeView(overlay)
            overlay = null
        }
    }

    private fun getOverlayType(): Int {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            @Suppress("DEPRECATION")
            WinLayoutParams.TYPE_SYSTEM_OVERLAY
        } else {
            WinLayoutParams.TYPE_APPLICATION_OVERLAY
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")
    }
}
