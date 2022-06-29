package com.ubb.citizen_u

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), Configuration.Provider, OnMapsSdkInitializedCallback {

    companion object {
        private const val TAG = "UBB-MainApplication"
    }

    @Inject
    lateinit var workFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(applicationContext, MapsInitializer.Renderer.LATEST, this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workFactory)
            .build()
    }

    override fun onMapsSdkInitialized(render: MapsInitializer.Renderer) {
        when (render) {
            MapsInitializer.Renderer.LATEST -> Log.d(TAG,
                "The latest version of the renderer is used: ")
            MapsInitializer.Renderer.LEGACY -> Log.d(TAG,
                "The legacy version of the renderer is used: ")
        }
    }
}