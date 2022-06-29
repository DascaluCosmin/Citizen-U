package com.ubb.citizen_u.util.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // Could also be set to BuildConfig.DEBUG / BuildConfig.RELEASE
    private const val USE_EMULATOR = false
    private const val EMULATOR_SPECIAL_IP = "10.0.2.2"
    private const val EMULATOR_SPECIAL_PORT_FIRESTORE = 8080
    private const val EMULATOR_SPECIAL_PORT_AUTH = 9099
    private const val EMULATOR_SPECIAL_PORT_STORAGE = 9199

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            if (USE_EMULATOR) {
                useEmulator(
                    EMULATOR_SPECIAL_IP,
                    EMULATOR_SPECIAL_PORT_FIRESTORE
                )
            }
        }
    }

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance().apply {
            if (USE_EMULATOR) {
                useEmulator(
                    EMULATOR_SPECIAL_IP,
                    EMULATOR_SPECIAL_PORT_AUTH
                )
            }
        }
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance().apply {
            if (USE_EMULATOR) {
                useEmulator(
                    EMULATOR_SPECIAL_IP,
                    EMULATOR_SPECIAL_PORT_STORAGE
                )
            }
        }
    }

    @Provides
    @Singleton
    fun providesFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }
}