package com.irancell.nwg.ios.presentation.main.viewModel

import AsyncResult
import androidx.lifecycle.*
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.repository.ProjectRepository
import com.irancell.nwg.ios.repository.RegionRepository
import com.irancell.nwg.ios.util.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class FragmentAuditMainViewModel @Inject constructor(
    private val regionRepository: RegionRepository,
    private val projectRepository: ProjectRepository,
    private val sharedPref: SharedPref
) : ViewModel() {
    var language = MutableLiveData(sharedPref.getString(SELECTED_LANGUAGE))
//    fun getAllRegion(): LiveData<List<RegionResponse>> =
//        regionRepository.getAllRegions().flowOn(Dispatchers.IO)
//            .asLiveData(viewModelScope.coroutineContext)


    fun getAllProjects() : LiveData<AsyncResult<List<ProjectDomain>>> =
        projectRepository.getAllProjects().flowOn(Dispatchers.IO)
            .asLiveData(viewModelScope.coroutineContext)

}


