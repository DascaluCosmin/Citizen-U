package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.Citizen
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.citizen.CitizenUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitizenViewModel @Inject constructor(
    private val citizenUseCases: CitizenUseCases
) : ViewModel() {

    companion object {
        private const val TAG = "UBB-CitizenViewModel"
    }

    private val _getCitizenState = MutableSharedFlow<Response<Citizen?>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val getCitizenState: SharedFlow<Response<Citizen?>> get() = _getCitizenState

    fun getCitizen(userId: String) {
        Log.d(TAG, "getCitizen: Getting citizen with id $userId")
        viewModelScope.launch(Dispatchers.IO) {
            citizenUseCases.getCitizenUseCase(userId).collect {
                _getCitizenState.tryEmit(it)
            }
        }
    }
}