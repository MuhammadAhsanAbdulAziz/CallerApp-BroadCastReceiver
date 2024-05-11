package com.example.incomingcalldetectionwithbroadcastreceiver

import android.content.Context
import android.content.Intent
import android.util.Log

class CountdownBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("CountdownBroadcast", "Alarm received!")
    }
}