package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ubb.citizen_u.data.model.citizens.Citizen
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.authentication.AuthenticationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases
) : ViewModel() {

    companion object {
        const val TAG = "UBB-AuthenticationViewModel"
    }

    private val _signInState = MutableSharedFlow<Response<FirebaseUser?>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val signInState: SharedFlow<Response<FirebaseUser?>> = _signInState

    private val _currentUserState = MutableSharedFlow<Response<Citizen?>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val currentUserState: SharedFlow<Response<Citizen?>> = _currentUserState

    private val _sendEmailResetUserPasswordState = MutableSharedFlow<Boolean>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val sendEmailResetUserPasswordState: SharedFlow<Boolean> = _sendEmailResetUserPasswordState

    private val _registerUserState = MutableSharedFlow<Response<Void>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val registerUserState: SharedFlow<Response<Void>> = _registerUserState

    fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn: Signing in user $email...")
        viewModelScope.launch(Dispatchers.IO) {
            authenticationUseCases.signInUseCase(email, password).collect {
                _signInState.tryEmit(it)
                _signInState.resetReplayCache()
            }
        }
    }

    fun signOut() {
        Log.d(TAG, "signOut: Signing out current user...")
        viewModelScope.launch(Dispatchers.IO) {
            authenticationUseCases.signOutUseCase()

            // TODO: Probably you have to rest the _currentUserState
        }
    }

    fun getCurrentUser() {
        Log.d(TAG, "getCurrentUser: Getting current user...")
        viewModelScope.launch(Dispatchers.IO) {
            authenticationUseCases.getCurrentUserUseCase().collect {
                delay(1000L)
                _currentUserState.tryEmit(it)

                // Here I did not use resetReplyCache because the first call to getCurrentUser()
                // is done on onViewCreated(), but the first collector is started in Fragment.STARTED
                // Thus, if I reset the cache, it will become empty and the collector will not be
                // able to collect the first value emitted here
            }
        }
    }

    fun sendEmailResetUserPassword(email: String) {
        Log.d(TAG, "sendEmailResetUserPassword: Sending email reset password for user $email...")
        viewModelScope.launch(Dispatchers.IO) {
            authenticationUseCases.resetUserPasswordUseCase(email).collect {
                _sendEmailResetUserPasswordState.tryEmit(it)
                _sendEmailResetUserPasswordState.resetReplayCache()
            }
        }
    }

    fun registerUser(email: String, password: String, citizen: Citizen) {
        Log.d(
            TAG,
            "registerUser: Registering user ${email}, citizen ${citizen.firstName}, ${citizen.lastName}"
        )
        viewModelScope.launch(Dispatchers.IO) {
            authenticationUseCases.registerUserUseCase(email, password, citizen).collect {
                _registerUserState.tryEmit(it)
            }
        }
    }
}