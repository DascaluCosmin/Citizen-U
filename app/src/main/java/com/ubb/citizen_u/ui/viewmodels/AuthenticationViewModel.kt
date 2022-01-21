package com.ubb.citizen_u.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.authentication.AuthenticationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases
) : ViewModel() {

    companion object {
        const val TAG = "AuthenticationViewModel"
    }

    private val _signInState = MutableStateFlow<Response<FirebaseUser?>>(Response.Initial)
    val signInState: StateFlow<Response<FirebaseUser?>> = _signInState

    private val _currentUserState = MutableStateFlow<FirebaseUser?>(null)
    val currentUserState: StateFlow<FirebaseUser?> = _currentUserState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authenticationUseCases.signIn(email, password).collect {
                _signInState.value = it
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            authenticationUseCases.getCurrentUser().collect {
                _currentUserState.value = it
            }
        }
    }

}