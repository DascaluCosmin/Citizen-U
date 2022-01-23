package com.ubb.citizen_u.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ubb.citizen_u.util.DatabaseConstants.USERS_COL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    // Could also be set to BuildConfig.DEBUG / BuildConfig.RELEASE
    private const val USE_EMULATOR = true
    private const val EMULATOR_SPECIAL_IP = "10.0.2.2"
    private const val EMULATOR_SPECIAL_PORT_FIRESTORE = 8080
    private const val EMULATOR_SPECIAL_PORT_AUTH = 9099

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
    @Named(USERS_COL)
    fun providesUsersRef(firebaseFirestore: FirebaseFirestore) =
        firebaseFirestore.collection(USERS_COL)
}