package com.vitysoft.android.keeplandscape

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val textView by lazy {
        findViewById<TextView>(R.id.textView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (!Settings.System.canWrite(this)
            || !Settings.canDrawOverlays(this)
        ) {
            Toast.makeText(this, R.string.request_permissions, Toast.LENGTH_LONG).show()
            if (!Settings.System.canWrite(this)) {
                var settings = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(settings)
            }
            if (!Settings.canDrawOverlays(this)){
                var settings = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settings)
            }

            textView.text = getString(R.string.no_permissions)
        } else {
            textView.text = ""

            forceLandscape(this)
            startKeepAliveService(this)
            finish()
        }
    }
}
