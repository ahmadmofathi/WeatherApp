package com.example.weatherapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Re-scheduling alarms after reboot")
            // Here you would typically read the saved alarm times from DataStore/DB
            // and call scheduleAlarm() for each one.
        }
    }
}
