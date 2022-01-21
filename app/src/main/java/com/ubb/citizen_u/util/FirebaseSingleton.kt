package com.ubb.citizen_u.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseSingleton {

    val FIREBASE = FirebaseUtil()

    class FirebaseUtil {

        companion object {
            // Could also be set to BuildConfig.DEBUG / BuildConfig.RELEASE
            const val USE_EMULATOR = true
            const val EMULATOR_SPECIAL_IP = "10.0.2.2"
            const val EMULATOR_SPECIAL_PORT_FIRESTORE = 8080
            const val EMULATOR_SPECIAL_PORT_AUTH = 9099
        }

        private var _firestore: FirebaseFirestore? = null
        val firestore: FirebaseFirestore
            get() {
                if (_firestore == null) {
                    _firestore = FirebaseFirestore.getInstance()

                    if (USE_EMULATOR) {
                        _firestore?.useEmulator(
                            EMULATOR_SPECIAL_IP,
                            EMULATOR_SPECIAL_PORT_FIRESTORE
                        )
                    }
                }
                return _firestore!!
            }

        private var _auth: FirebaseAuth? = null
        val auth: FirebaseAuth
            get() {
                if (_auth == null) {
                    _auth = FirebaseAuth.getInstance()

                    if (USE_EMULATOR) {
                        _auth?.useEmulator(EMULATOR_SPECIAL_IP, EMULATOR_SPECIAL_PORT_AUTH)
                    }
                }
                return _auth!!
            }
    }
}

