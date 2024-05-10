package com.example.incomingcalldetectionwithbroadcastreceiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

open class BroadcastReceiver : BroadcastReceiver() {

    private lateinit var context: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            this.context = context
        }
        if (context != null && intent != null) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val callingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if (callingNumber != null) {
                    Toast.makeText(context, callingNumber, Toast.LENGTH_SHORT).show()
                    if(callingNumber == "03408372464") {
                        try {
                            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                            val intent1 = Intent(context, MainActivity::class.java)
                            intent1.putExtra("number", callingNumber)
                            intent1.addFlags(FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent1)
                            val background: Thread = object : Thread() {
                                override fun run() {
                                    try {
                                        sleep((2 * 1000).toLong())
                                        acceptCall(telecomManager)
                                        val finishIntent = Intent("com.example.FINISH_ACTIVITY")
                                        context.sendBroadcast(finishIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context,e.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            background.start()
                        } catch (e: Exception) {
                            // Log any exceptions
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    fun acceptCall(telecomManager: TelecomManager) {
        // Request to accept incoming call
        // This requires the android.permission.ANSWER_PHONE_CALLS permission
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telecomManager.acceptRingingCall()
            }
        }
    }
//
//    fun rejectCall(telecomManager: TelecomManager) {
//        // Request to reject incoming call
//        // This requires the android.permission.ANSWER_PHONE_CALLS permission
//        if (ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.READ_PHONE_STATE
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                telecomManager.endCall()
//            }
//        }
//    }
}