package com.vitysoft.android.keeplandscape

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        val permissionsText: TextView
        var intentSettings: Intent

        if (!Settings.System.canWrite(this)) {
            Toast.makeText(
                this,
                "Please select " + getString(R.string.app_name) + " and allow permissions first!",
                Toast.LENGTH_LONG
            ).show()

            if (!Settings.System.canWrite(this)) {
                intentSettings = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intentSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentSettings)
            }
            permissionsText = findViewById<View>(R.id.permissions) as TextView
            permissionsText.setText("Required permissions not granted! Please restart this app")
            permissionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        } else {
            permissionsText = findViewById<View>(R.id.permissions) as TextView
            permissionsText.text = ""
            permissionsText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0f)

            forceLandscape(this)
            startKeepAliveService(this)
            finish()

//            Log.d("KeepLandscape", "sendBroadcast")
//            val intent = Intent(ACTION_START_LANDSCAPE)
//            intent.setClass(this, StartLandscapeReceiver::class.java)
//            sendBroadcast(intent)
        }
    }
}

fun forceLandscape(context: Context) {
    Log.d("KeepLandscape", "forceLandscape")
    val cr = context.contentResolver
    Settings.System.putInt(cr, Settings.System.USER_ROTATION, 1)
    Settings.System.putInt(cr, Settings.System.ACCELEROMETER_ROTATION, 1)
}

fun startKeepAliveService(context: Context) {
    Log.d("KeepLandscape", "startKeepAliveService")
    val service = Intent(context, KeepAliveService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        context.startForegroundService(service)
    } else {
        context.startService(service)
    }
}
