package com.example.incomingcalldetectionwithbroadcastreceiver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

//    private lateinit var callReceiver: BroadcastReceiver
//    private lateinit var telecomManager: TelecomManager
    private var phoneNumber: String = ""
    private lateinit var numText:TextView
    private val finishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.FINISH_ACTIVITY") {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAndRequestPermissions(this)
        checkOverlayPermission(this)

//        // Request necessary permissions if not granted
//        requestPermissionsIfNeeded()
//
//        // Initialize CallReceiver
//        callReceiver = BroadcastReceiver()
//
//        // Register CallReceiver to listen for incoming calls
//        registerReceiver(callReceiver, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
//
//        telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        phoneNumber = intent.getStringExtra("number").toString()


        // Example buttons to accept or reject calls (You can replace with your UI components)
//        val acceptCallButton: Button = findViewById(R.id.acceptCallButton)
//        val rejectCallButton: Button = findViewById(R.id.rejectCallButton)
         numText = findViewById(R.id.numText)
//
//
        if (intent != null && intent.hasExtra("number")) {
            val incomingNumber = intent.getStringExtra("number")
            numText.text = incomingNumber
        }
//
//        acceptCallButton.setOnClickListener {
//            // Call acceptCall method
//            callReceiver.acceptCall(telecomManager)
//        }
//
//        rejectCallButton.setOnClickListener {
//            // Call rejectCall method
//            callReceiver.rejectCall(telecomManager)
//            Toast.makeText(this, "Call rejected", Toast.LENGTH_SHORT).show()
//        }
            registerReceiver(finishReceiver, IntentFilter("com.example.FINISH_ACTIVITY"))


    }

    private fun checkAndRequestPermissions(activity: Activity) {
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.READ_CALL_LOG)
        }
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.READ_CONTACTS)
        }
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.READ_PHONE_STATE)
        }
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionsToRequest.add(android.Manifest.permission.ANSWER_PHONE_CALLS)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    private fun checkOverlayPermission(context: Context) {
        if (!Settings.canDrawOverlays(context)) {
            // Permission is not granted, open app settings to grant the permission
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${context.packageName}")
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Failed to open app settings
                Toast.makeText(context, "Failed to open app settings", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Check if the activity was brought back to the foreground with a new intent containing the number
        if (intent != null && intent.hasExtra("number")) {
            val incomingNumber = intent.getStringExtra("number")
            numText.text = incomingNumber
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver when the activity is destroyed
        numText.text = ""
//        unregisterReceiver(callReceiver)
    }

//    private fun requestPermissionsIfNeeded() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val permissionsToRequest = mutableListOf<String>()
//            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                permissionsToRequest.add(android.Manifest.permission.READ_PHONE_STATE)
//            }
//            if (checkSelfPermission(android.Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
//                permissionsToRequest.add(android.Manifest.permission.ANSWER_PHONE_CALLS)
//            }
//            if (permissionsToRequest.isNotEmpty()) {
//                requestPermissions(permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
//            }
//        }
//    }
//
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}