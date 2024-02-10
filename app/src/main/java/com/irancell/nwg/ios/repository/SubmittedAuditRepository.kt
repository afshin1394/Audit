package com.irancell.nwg.ios.repository

import AsyncResult
import com.irancell.nwg.ios.data.model.AuditStateDomain
import com.irancell.nwg.ios.data.remote.response.asDomainModel
import com.irancell.nwg.ios.network.get.SubmittedAuditService
import com.irancell.nwg.ios.util.apiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubmittedAuditRepository @Inject constructor(
    private val submittedAuditService: SubmittedAuditService
) {
    fun getSiteAudit(audit_id : Int) : Flow<AsyncResult<AuditStateDomain>> {
        return apiCall(true, preProcess =  {},{ submittedAuditService.getSubmittedAudit(audit_id)  } , postProcess =  {
            it.asDomainModel()
        })

    }
}