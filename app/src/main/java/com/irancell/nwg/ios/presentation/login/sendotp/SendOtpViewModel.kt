package com.irancell.nwg.ios.presentation.login.sendotp

import AsyncResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.irancell.nwg.ios.data.remote.response.SessionResponse
import com.irancell.nwg.ios.repository.AuthRepository
import com.irancell.nwg.ios.util.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel

import javax.inject.Inject


@HiltViewModel
class SendOtpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sharedPref: SharedPref,
    private val isOnline : Boolean

    ) : ViewModel() {
    fun requestSession(username: String, password: String): LiveData<AsyncResult<SessionResponse>> =
        authRepository.requestSession(username, password).asLiveData(viewModelScope.coroutineContext)


}

