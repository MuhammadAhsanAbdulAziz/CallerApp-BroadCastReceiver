package com.example.incomingcalldetectionwithbroadcastreceiver

import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
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
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var utilManager: UtilManager
    private lateinit var countDownTimer: CountDownTimer


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
                                utilManager.saveData("3day", "duration")
                            }

                            R.id.day7radio -> {
                                utilManager.saveData("7day", "duration")
                            }

                            R.id.day30radio -> {
                                utilManager.saveData("30day", "duration")
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
                            val duration = utilManager.getData("duration")
                            when(duration){
                                "1hr" ->{
                                    startCountdown(Calendar.HOUR_OF_DAY,1)
                                }
                                "3hr" ->{
                                    startCountdown(Calendar.HOUR_OF_DAY,3)
                                }
                                "6hr" ->{
                                    startCountdown(Calendar.HOUR_OF_DAY,6)
                                }
                                "24hr" ->{
                                    startCountdown(Calendar.HOUR_OF_DAY,24)
                                }
                                "3day" ->{
                                    startCountdown(Calendar.DAY_OF_MONTH,3)
                                }
                                "7day" ->{
                                    startCountdown(Calendar.DAY_OF_MONTH,7)
                                }
                                "30day" ->{
                                    startCountdown(Calendar.DAY_OF_MONTH,30)
                                }
                            }
                            contactRad.isChecked = true
                            alertDialogTwo.dismiss()
                        }

                    }
                    alertDialogTwo.show()
                }
            }
        }

        checkAndRequestPermissions(this)
    }


    private fun startCountdown(time:Int,duration:Int) {
        val targetTime = Calendar.getInstance().apply {
            add(time, duration)
        }.timeInMillis
        countDownTimer = object : CountDownTimer(targetTime - System.currentTimeMillis(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Update remaining time in UI
                val remainingSeconds = millisUntilFinished / 1000
                val hours = remainingSeconds / 3600
                val minutes = (remainingSeconds % 3600) / 60
                val seconds = remainingSeconds % 60
            }

            override fun onFinish() {
                utilManager.saveData("","duration")
                utilManager.saveData("accept","saved")
            }
        }.start()
    }

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

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}