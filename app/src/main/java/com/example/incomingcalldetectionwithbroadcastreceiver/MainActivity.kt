package com.example.incomingcalldetectionwithbroadcastreceiver

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText


class MainActivity : AppCompatActivity() {

    //    private var phoneNumber: String = ""
//    private lateinit var numText: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var utilManager: UtilManager
    private val finishReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.FINISH_ACTIVITY") {
                finish()
            }
        }
    }
//    private val permissions = arrayOf(
//        android.Manifest.permission.READ_CALL_LOG,
//        android.Manifest.permission.READ_CONTACTS,
//        android.Manifest.permission.READ_PHONE_STATE,
//        android.Manifest.permission.ANSWER_PHONE_CALLS,
//        android.Manifest.permission.READ_SMS,
//        android.Manifest.permission.SEND_SMS
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val acceptRad: RadioButton = findViewById(R.id.acceptAllRadio)
        val contactRad: RadioButton = findViewById(R.id.onlyMyContactsRadio)
        utilManager = UtilManager(this)
        radioGroup = findViewById(R.id.radioGroup)
        val option = utilManager.getData("saved")
        if (option != null) {
            if (option == "accept") {
                acceptRad.isChecked = true
            } else {
                contactRad.isChecked = true
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.acceptAllRadio -> {
                    utilManager.saveData("accept", "saved")
                }

                R.id.onlyMyContactsRadio -> {
                    val builderTwo: AlertDialog.Builder = AlertDialog.Builder(this)
                    val inflaterTwo: LayoutInflater = this.layoutInflater
                    val dialogviewTwo: View =
                        inflaterTwo.inflate(R.layout.only_my_contact_custom_dialog, null)
                    builderTwo.setView(dialogviewTwo)
                    val alertDialogTwo: AlertDialog = builderTwo.create()
                    alertDialogTwo.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    alertDialogTwo.setCancelable(false)
                    alertDialogTwo.setCanceledOnTouchOutside(false)
                    val cancelBtn: Button = dialogviewTwo.findViewById(R.id.cancelBtn)
                    val saveBtn: Button = dialogviewTwo.findViewById(R.id.saveBtn)
                    val smsCheck: CheckBox = dialogviewTwo.findViewById(R.id.smsCheck)
                    val smsText: TextInputEditText = dialogviewTwo.findViewById(R.id.smsText)
                    val radioGroup: RadioGroup = dialogviewTwo.findViewById(R.id.dialogRadioGroup)
                    radioGroup.setOnCheckedChangeListener { _, checkedId ->
                        when (checkedId) {
                            R.id.hr1radio -> {
                                utilManager.saveData("1hr", "duration")
                                Toast.makeText(this, "1hr", Toast.LENGTH_SHORT).show()
                            }

                            R.id.hr3radio -> {
                                utilManager.saveData("3hr", "duration")
                                Toast.makeText(this, "3hr", Toast.LENGTH_SHORT).show()
                            }

                            R.id.hr6radio -> {
                                utilManager.saveData("6hr", "duration")
                                Toast.makeText(this, "6hr", Toast.LENGTH_SHORT).show()
                            }

                            R.id.hr24radio -> {
                                utilManager.saveData("24hr", "duration")
                            }

                            R.id.day3radio -> {
                                utilManager.saveData("3hr", "duration")
                            }

                            R.id.day7radio -> {
                                utilManager.saveData("7hr", "duration")
                            }

                            R.id.day30radio -> {
                                utilManager.saveData("30hr", "duration")
                            }
                        }
                    }
                    if (smsCheck.isChecked) {
                        utilManager.saveData("1", "sms")
                    } else {
                        utilManager.saveData("0", "sms")
                    }
                    cancelBtn.setOnClickListener {
                        if (option != null) {
                            if (option == "accept") {
                                acceptRad.isChecked = true
                                utilManager.saveData("","duration")
                            } else {
                                contactRad.isChecked = true
                            }
                        } else {
                            utilManager.saveData("","duration")
                            contactRad.isChecked = false
                        }
                        alertDialogTwo.dismiss()
                    }
                    saveBtn.setOnClickListener {
                        val message = smsText.text!!.trim().toString()
                        if (message.isNotEmpty()) {
                            utilManager.saveData(message, "message")
                        }

                        if (utilManager.getData("duration") != null && utilManager.getData("duration") != "") {
                            utilManager.saveData("contacts", "saved")
                            contactRad.isChecked = true
                            alertDialogTwo.dismiss()
                        }

                    }
                    alertDialogTwo.show()
                }
            }
        }

//        checkOverlayPermission(this)
//        phoneNumber = intent.getStringExtra("number").toString()
//        numText = findViewById(R.id.numText)
//        if (intent != null && intent.hasExtra("number")) {
//            val incomingNumber = intent.getStringExtra("number")
//            numText.text = incomingNumber
//        }
        registerReceiver(finishReceiver, IntentFilter("com.example.FINISH_ACTIVITY"))
        checkAndRequestPermissions(this)

//
//        if(!checkAndRequestPermissions(this)){
//            Toast.makeText(this, "Kindly give full permissions, closing...", Toast.LENGTH_SHORT).show()
//            val background: Thread = object : Thread() {
//                override fun run() {
//                    try {
//                        sleep((2 * 1000).toLong())
//                        finish()
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//            background.start()
//        }
    }
//
//    private fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
//        for (permission in permissions) {
//            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                // Return false if any of the permissions is not granted
//                return false
//            }
//        }
//        // All permissions are granted
//        return true
//    }

//    private fun checkAndRequestPermissions(activity: Activity) : Boolean {
//        val permissionsToRequest = mutableListOf<String>()
//
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                android.Manifest.permission.READ_CALL_LOG
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(android.Manifest.permission.READ_CALL_LOG)
//        }
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                android.Manifest.permission.READ_CONTACTS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(android.Manifest.permission.READ_CONTACTS)
//        }
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                android.Manifest.permission.READ_PHONE_STATE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(android.Manifest.permission.READ_PHONE_STATE)
//        }
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                android.Manifest.permission.ANSWER_PHONE_CALLS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                permissionsToRequest.add(android.Manifest.permission.ANSWER_PHONE_CALLS)
//            }
//        }
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                android.Manifest.permission.READ_SMS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(android.Manifest.permission.READ_SMS)
//        }
//        if (ContextCompat.checkSelfPermission(
//                activity,
//                android.Manifest.permission.SEND_SMS
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            permissionsToRequest.add(android.Manifest.permission.SEND_SMS)
//        }
//
//        if (permissionsToRequest.isNotEmpty()) {
//            ActivityCompat.requestPermissions(
//                activity,
//                permissionsToRequest.toTypedArray(),
//                PERMISSION_REQUEST_CODE
//            )
//        }
//        return arePermissionsGranted(this, permissions)
//    }
//
//    private fun checkOverlayPermission(context: Context) {
//        if (!Settings.canDrawOverlays(context)) {
//            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//            intent.data = Uri.parse("package:${context.packageName}")
//            try {
//                context.startActivity(intent)
//            } catch (e: Exception) {
//                Toast.makeText(context, "Failed to open app settings", Toast.LENGTH_SHORT).show()
//                e.printStackTrace()
//            }
//        }
//    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        // Check if the activity was brought back to the foreground with a new intent containing the number
//        if (intent != null && intent.hasExtra("number")) {
//            val incomingNumber = intent.getStringExtra("number")
//            numText.text = incomingNumber
//        }
//    }

    private fun checkAndRequestPermissions(activity: Activity) {
        val permissionsToRequest = mutableListOf<String>()

        val permissions = arrayOf(
            android.Manifest.permission.READ_CALL_LOG,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.ANSWER_PHONE_CALLS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.SEND_SMS
        )

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    activity, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (!Settings.canDrawOverlays(activity)) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (!Settings.canDrawOverlays(activity)) {
                permissionsToRequest.add(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            var deniedPermission: String? = null
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission = permissions[i]
                    break
                }
            }

            if (deniedPermission != null) {
                val message = "Permission denied: $deniedPermission"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 3000)
            }
        }
    }


    //    override fun onDestroy() {
//        super.onDestroy()
//        numText.text = ""
//    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}