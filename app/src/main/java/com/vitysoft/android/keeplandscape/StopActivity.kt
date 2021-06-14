package com.vitysoft.android.keeplandscape

import android.app.VoiceInteractor
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)

        val button = findViewById<Button>(R.id.stopButton)
        button.setOnClickListener {
            stopKeepAliveService()
            finish()
        }
    }

    private fun stopKeepAliveService() {
        val service = Intent(this, KeepAliveService::class.java)
        stopService(service)
    }
}
