package com.irancell.nwg.ios.presentation.main.viewModel

import AsyncResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.irancell.nwg.ios.data.model.profile.ProfileDomain
import com.irancell.nwg.ios.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FragmentProfileMainViewModel @Inject constructor(
private val profileRepository: ProfileRepository,
) : ViewModel() {


    fun getProfile() : LiveData<AsyncResult<List<ProfileDomain>>>
    {
      return profileRepository.getProfileLocal().asLiveData(viewModelScope.coroutineContext)
    }
}