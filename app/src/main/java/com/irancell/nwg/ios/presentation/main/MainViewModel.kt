package com.irancell.nwg.ios.presentation.main

import AsyncResult
import androidx.lifecycle.*
import com.irancell.nwg.ios.data.model.profile.ProfileDomain
import com.irancell.nwg.ios.repository.AuthRepository
import com.irancell.nwg.ios.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val profileRepository : ProfileRepository,
    private val isOnline : Boolean,
    private val authRepository: AuthRepository,
) : ViewModel() {

   fun getProfile(): LiveData<AsyncResult<List<ProfileDomain>>>{
     return  profileRepository.getProfileLocal().asLiveData(viewModelScope.coroutineContext)
   }

    fun logout() : LiveData<AsyncResult<Unit>> =
        authRepository.logout()
            .asLiveData(viewModelScope.coroutineContext)




}