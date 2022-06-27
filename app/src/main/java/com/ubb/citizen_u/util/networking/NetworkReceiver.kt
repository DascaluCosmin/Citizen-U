package com.ubb.citizen_u.util.networking

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NetworkReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "UBB-NetworkReceiver"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (InternetConnection.isOnline(p0!!)) {
            Log.d(TAG, "onReceive: has internet connection")
        } else {
            Log.d(TAG, "onReceive: doesn't have internet connection")
        }
    }
}