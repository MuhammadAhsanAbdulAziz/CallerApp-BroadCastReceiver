package com.example.incomingcalldetectionwithbroadcastreceiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NumberDisplayActivity : AppCompatActivity() {

    private lateinit var numberTxt: TextView
    private val finishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.FINISH_ACTIVITY") {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_display)

        numberTxt = findViewById(R.id.numberTxt)

        if (intent != null && intent.hasExtra("number")) {
            val incomingNumber = intent.getStringExtra("number")
            numberTxt.text = incomingNumber
        }

        registerReceiver(finishReceiver, IntentFilter("com.example.FINISH_ACTIVITY"))

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Check if the activity was brought back to the foreground with a new intent containing the number
        if (intent != null && intent.hasExtra("number")) {
            val incomingNumber = intent.getStringExtra("number")
            numberTxt.text = incomingNumber
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        numberTxt.text = ""
    }
}

