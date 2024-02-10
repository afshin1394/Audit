package com.irancell.nwg.ios.presentation.main.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.irancell.nwg.ios.data.model.SiteState
import com.irancell.nwg.ios.repository.SiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class FragmentHomeViewModel @Inject constructor(
    private val siteRepository: SiteRepository
) : ViewModel(){

fun getSiteStatusDetails() : LiveData<List<SiteState>>{
   return siteRepository.getSiteStateSituation().flowOn(Dispatchers.IO).asLiveData(viewModelScope.coroutineContext)
}



}