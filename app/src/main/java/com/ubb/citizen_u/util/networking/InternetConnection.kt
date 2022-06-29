package com.ubb.citizen_u.util.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class InternetConnection {

    companion object {
        private const val TAG = "UBB-InternetConnection"

        fun isOnline(context: Context): Boolean {
            Log.d(TAG, "isOnline: x")
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.d(TAG, "isOnline: NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.d(TAG, "isOnline: NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }

                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        Log.d(TAG, "isOnline: NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
            return false
        }
    }
}