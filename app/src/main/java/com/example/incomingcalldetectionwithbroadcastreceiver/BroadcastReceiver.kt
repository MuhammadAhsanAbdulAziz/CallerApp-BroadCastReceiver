package com.example.incomingcalldetectionwithbroadcastreceiver

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService


open class BroadcastReceiver : BroadcastReceiver() {

    private lateinit var context: Context
    private lateinit var utilManager: UtilManager
    private lateinit var deviceManger : DevicePolicyManager
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            this.context = context
            utilManager = UtilManager(context)
            deviceManger = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        }
        if (context != null && intent != null) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val callingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                if (callingNumber != null) {
                    val telecomManager =
                        context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                    if (utilManager.getData("saved") != null) {
                        if (utilManager.getData("saved") == "accept") {
                            try {
                                val intent1 = Intent(context, NumberDisplayActivity::class.java)
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
                                            deviceManger.lockNow()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, e.message, Toast.LENGTH_LONG)
                                                .show()
                                        }
                                    }
                                }
                                background.start()
                            } catch (e: Exception) {
                                // Log any exceptions
                                e.printStackTrace()
                            }
                        } else if (utilManager.getData("saved") == "contacts") {
                            if (getContactDisplayNameByNumber(callingNumber) != "?" && getContactDisplayNameByNumber(
                                    callingNumber
                                ) != "Unknown"
                            ) {
                                try {
                                    val intent1 = Intent(context, NumberDisplayActivity::class.java)
                                    intent1.putExtra("number", callingNumber)
                                    intent1.addFlags(FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(intent1)
                                    val background: Thread = object : Thread() {
                                        override fun run() {
                                            try {
                                                sleep((2 * 1000).toLong())
                                                acceptCall(telecomManager)
                                                val finishIntent =
                                                    Intent("com.example.FINISH_ACTIVITY")
                                                context.sendBroadcast(finishIntent)
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context, e.message, Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }
                                    background.start()
                                } catch (e: Exception) {
                                    // Log any exceptions
                                    e.printStackTrace()
                                }
                            } else {
                                declineCall(telecomManager)
                            }
                        }
                    }


                }
            }
        }
    }

    private fun getContactDisplayNameByNumber(number: String?): String {
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        var name = "?"
        val contentResolver: ContentResolver = context.contentResolver
        val contactLookup = contentResolver.query(
            uri, arrayOf(
                BaseColumns._ID, PhoneLookup.DISPLAY_NAME
            ), null, null, null
        )
        try {
            if (contactLookup != null && contactLookup.count > 0) {
                contactLookup.moveToNext()
                val nameColumnIndex =
                    contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)
                name = if (nameColumnIndex != -1) {
                    contactLookup.getString(nameColumnIndex)
                } else {
                    // Handle the case when the DISPLAY_NAME column doesn't exist in the cursor
                    "Unknown"
                }
            }
        } finally {
            contactLookup?.close()
        }
        return name
    }

    fun acceptCall(telecomManager: TelecomManager) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telecomManager.acceptRingingCall()

            }
        }
    }

    private fun declineCall(telecomManager: TelecomManager) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                telecomManager.endCall()

            }
        }
    }
}