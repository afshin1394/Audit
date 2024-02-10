package com.irancell.nwg.ios.presentation.splash

import AsyncResult
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.irancell.nwg.ios.IMAGE_QUALITY
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.model.asDatabaseModel
import com.irancell.nwg.ios.data.model.profile.ProfileDomain
import com.irancell.nwg.ios.repository.*
import com.irancell.nwg.ios.util.HIGH
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.ENGLISH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val exceptionRepository: ExceptionRepository,
    private val sharedPref: SharedPref,
    val location: LiveData<Location>,
) : ViewModel() {
    fun setDefaultSetting(language: String, quality: String) {
        viewModelScope.launch {
            if(sharedPref.getString(SELECTED_LANGUAGE).equals(""))
            sharedPref.putString(SELECTED_LANGUAGE, ENGLISH)
            if (sharedPref.getString(IMAGE_QUALITY).equals(""))
            sharedPref.putString(IMAGE_QUALITY, HIGH)
        }
    }

    fun getProfile(): LiveData<AsyncResult<List<ProfileDomain>>> =
        profileRepository.getProfileRemote().asLiveData(viewModelScope.coroutineContext)

    fun insertException(exception: com.irancell.nwg.ios.data.model.Exception) : LiveData<AsyncResult<Long>>{
       return exceptionRepository.insertException(exception.asDatabaseModel()).asLiveData(viewModelScope.coroutineContext)
    }

    fun getAllExceptions() : LiveData<AsyncResult<List<com.irancell.nwg.ios.data.model.Exception>>>{
        return exceptionRepository.getAll().asLiveData(viewModelScope.coroutineContext)
    }

    fun deleteAllExceptions() : LiveData<AsyncResult<Unit>>{
        return exceptionRepository.deleteAll().asLiveData(viewModelScope.coroutineContext)
    }




}