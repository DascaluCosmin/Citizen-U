package com.ubb.citizen_u.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ubb.citizen_u.data.model.PublicSpending
import com.ubb.citizen_u.domain.model.Response
import com.ubb.citizen_u.domain.usescases.publicspending.PublicSpendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicSpendingViewModel @Inject constructor(
    private val publicSpendingUseCase: PublicSpendingUseCase,
) : ViewModel() {

    companion object {
        const val TAG = "UBB-PublicSpendingViewModel"
    }

    private val _listPublicSpending: MutableList<PublicSpending?> = mutableListOf()
    val listPublicSpending: List<PublicSpending?> get() = _listPublicSpending

    private val _getAllPublicSpendingState: MutableSharedFlow<Response<List<PublicSpending?>>> =
        MutableSharedFlow(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    val getAllPublicSpendingState: SharedFlow<Response<List<PublicSpending?>>> =
        _getAllPublicSpendingState

    fun getAllPublicSpending() {
        Log.d(TAG, "Getting all public spending...")
        viewModelScope.launch(Dispatchers.IO) {
            publicSpendingUseCase.getAllPublicSpendingUseCase().collect {
                if (it is Response.Success) {
                    _listPublicSpending.clear()
                    _listPublicSpending.addAll(it.data)
                }
                _getAllPublicSpendingState.tryEmit(it)
            }
        }
    }
}