package com.example.incomingcalldetectionwithbroadcastreceiver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class CallService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("number")) {
            val phoneNumber = intent.getStringExtra("number").toString()
            if (phoneNumber.isNotEmpty()) {
                Toast.makeText(this, "incoming call", Toast.LENGTH_SHORT).show()
                startMainActivity(phoneNumber)
            }
        }
        return START_NOT_STICKY
    }

    private fun startMainActivity(phoneNumber: String) {
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.setAction(Intent.ACTION_VIEW)
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        mainActivityIntent.putExtra("number", phoneNumber)
        startActivity(mainActivityIntent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

