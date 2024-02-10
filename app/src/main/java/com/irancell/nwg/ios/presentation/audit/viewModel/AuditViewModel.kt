package com.irancell.nwg.ios.presentation.audit.viewModel


import AsyncResult
import androidx.lifecycle.*
import androidx.work.ListenableWorker
import androidx.work.WorkInfo
import com.google.common.util.concurrent.ListenableFuture
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.model.AuditStateDomain
import com.irancell.nwg.ios.data.model.MandatoryField
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.model.TabModel
import com.irancell.nwg.ios.data.model.form.FormDomain
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.repository.*
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.MAIN
import com.irancell.nwg.ios.util.constants.PROBLEMATIC
import com.irancell.nwg.ios.util.constants.Problematic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.jar.Attributes.Name
import javax.inject.Inject
import kotlin.coroutines.coroutineContext


@HiltViewModel
class AuditViewModel @Inject constructor(
    private val auditFormRepository: AuditFormRepository,
    private val problematicFormRepository: ProblematicFormRepository,
    private val uploadAuditRepository: UploadAuditRepository,
    private val submittedAuditRepository: SubmittedAuditRepository,
    private val siteRepository: SiteRepository,
    private val sharedPref: SharedPref,
    private val gpsListenerRepository : GpsListenerRepository
) : ViewModel() {
    val TAG = AuditViewModel::class.java.simpleName

    var language = MutableLiveData(sharedPref.getString(SELECTED_LANGUAGE))

//    fun startGpsListener() : LiveData<WorkInfo>{
//        return gpsListenerRepository.listenForGpsSignal()
//    }

    fun getAttributeGroups(siteId: Int?, type: Int): LiveData<List<TabModel>> {
        return when (type) {
            MAIN -> {
                auditFormRepository.getAttributeGroups(siteId, type).flowOn(Dispatchers.IO)
                    .asLiveData(viewModelScope.coroutineContext)
            }
            PROBLEMATIC -> {
                problematicFormRepository.getAttributeGroups(siteId, type).flowOn(Dispatchers.IO)
                    .asLiveData(viewModelScope.coroutineContext)
            }
            else -> {
                auditFormRepository.getAttributeGroups(siteId, type).flowOn(Dispatchers.IO)
                    .asLiveData(viewModelScope.coroutineContext)
            }
        }
    }

    fun getAuditPerSite(siteId: Int, type: Int): LiveData<AsyncResult<SubmitAuditRequest>> {
        return uploadAuditRepository.getAllSiteGroups(siteId, type)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun submitAudit(
        audit_id: Int,
        body: SubmitAuditRequest,
        type: Int
    ): LiveData<AsyncResult<Unit>> {
        return uploadAuditRepository.submitAudit(audit_id, body, type)
            .asLiveData(viewModelScope.coroutineContext)
    }

    fun uploadAudit(
        fileDirPath: String,
        siteDomain: SiteDomain,
        type: Int
    ): LiveData<WorkInfo> {
        return uploadAuditRepository.uploadAudit(fileDirPath, siteDomain, type)

    }

    fun getSubmittedAuditState(audit_id: Int): LiveData<AsyncResult<AuditStateDomain>> {
        return submittedAuditRepository.getSiteAudit(audit_id)
            .asLiveData(viewModelScope.coroutineContext)

    }

    fun updateSiteByAuditId(auditId: Int, state: Int) {
        viewModelScope.launch {
            siteRepository.updateSiteStateByAuditId(auditId, state)
        }
    }

    fun getAuditState(siteId: Int): LiveData<AsyncResult<Int>> {
        return siteRepository.getAuditState(siteId).asLiveData(viewModelScope.coroutineContext)
    }

    fun checkForMandatoryFields(
        siteId: Int,
        type: Int
    ): LiveData<AsyncResult<List<MandatoryField>>> {
        return when (type) {
            PROBLEMATIC -> {
                problematicFormRepository.getAllAttributesBaseOnSite(siteId)
                    .asLiveData(viewModelScope.coroutineContext)

            }
            MAIN -> {
                return auditFormRepository.getAllAttributesBaseOnSite(siteId)
                    .asLiveData(viewModelScope.coroutineContext)

            }
            else -> {
                problematicFormRepository.getAllAttributesBaseOnSite(siteId)
                    .asLiveData(viewModelScope.coroutineContext)
            }
        }
    }


}