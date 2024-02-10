package com.irancell.nwg.ios.presentation.login.verifyotp

import AsyncResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.data.model.profile.ProfileDomain
import com.irancell.nwg.ios.data.remote.response.AuditFormResponse
import com.irancell.nwg.ios.data.remote.response.Site
import com.irancell.nwg.ios.data.remote.response.TokenResponse
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.repository.*
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.SESSION_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyOtpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val projectRepository: ProjectRepository,
    private val siteRepository: SiteRepository,
    private val auditFormRepository: AuditFormRepository,
    private val generateAuditRepository: GenerateAuditRepository,
    private val problematicFormRepository : ProblematicFormRepository,
    private val sharedPref: SharedPref,
    private val isOnline: Boolean
) : ViewModel() {
    fun requestToken(code: String): LiveData<AsyncResult<TokenResponse>> {
        return authRepository.requestToken(sharedPref.getString(SESSION_ID)!!, code).asLiveData(viewModelScope.coroutineContext)
    }

    fun resendVerifyCode(): LiveData<AsyncResult<Unit>> {
        return authRepository.resendVerifyCode(sharedPref.getString(SESSION_ID)!!).asLiveData(viewModelScope.coroutineContext)
    }

    fun getProfile(): LiveData<AsyncResult<List<ProfileDomain>>> =
        profileRepository.getProfileRemote().asLiveData(viewModelScope.coroutineContext)

    fun updateProjects(): LiveData<AsyncResult<List<ProjectDomain>>> {
        return projectRepository.fetchProjects().asLiveData(viewModelScope.coroutineContext)
    }

    fun getProjectIds(): LiveData<AsyncResult<List<Int>>> {
        return projectRepository.getProjectIds().asLiveData(viewModelScope.coroutineContext)
    }

    fun updateSites(projectId: List<Int>): LiveData<AsyncResult<ArrayList<Site>>> {
        return siteRepository.fetchSitesByProjectId(projectId)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun updateAuditFormat(): LiveData<AsyncResult<Unit>> {
        return auditFormRepository.updateAuditForm().asLiveData(viewModelScope.coroutineContext)
    }

    fun updateGenerateAudits(): LiveData<AsyncResult<List<ItemResponse>>> {
        return generateAuditRepository.fetchGenerateAudits()
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun updateProblematicForms() : LiveData<AsyncResult<AuditFormResponse>>{
        return problematicFormRepository.fetchProblematicForm().asLiveData(viewModelScope.coroutineContext)
    }
}