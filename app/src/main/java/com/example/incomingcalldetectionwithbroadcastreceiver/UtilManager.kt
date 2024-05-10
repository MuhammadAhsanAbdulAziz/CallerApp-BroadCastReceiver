package com.example.incomingcalldetectionwithbroadcastreceiver

import android.content.Context
import com.example.incomingcalldetectionwithbroadcastreceiver.Constants.PREFS_TOKEN_FILE

class UtilManager(context: Context) {
    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveData(data: String?, key: String) {
        val editor = prefs.edit()
        editor.putString(key, data)
        editor.apply()
    }

    fun getData(key: String): String? {
        return prefs.getString(key, null)
    }

}