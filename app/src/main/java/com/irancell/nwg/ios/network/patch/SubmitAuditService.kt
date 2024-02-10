package com.irancell.nwg.ios.network.patch

import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import com.irancell.nwg.ios.data.remote.response.AuditFormResponse
import retrofit2.Call
import retrofit2.http.*

interface SubmitAuditService {
    @PATCH("audit/submit-audit-project/{id}/")
    fun patchSubmitAudit(@Path("id") audit_id : Int,@Body submitAuditRequest: SubmitAuditRequest): Call<Unit>
}