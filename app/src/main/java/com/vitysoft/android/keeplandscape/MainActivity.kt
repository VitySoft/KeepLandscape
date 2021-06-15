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
        val textView: TextView
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
            textView = findViewById<View>(R.id.textView) as TextView
            textView.text = "Required permissions not granted! Please restart this app"
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        } else {
            textView = findViewById<View>(R.id.textView) as TextView
            textView.text = ""
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 0f)

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
