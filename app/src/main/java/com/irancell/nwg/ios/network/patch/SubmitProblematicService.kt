package com.irancell.nwg.ios.network.patch

import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface SubmitProblematicService {
    @PATCH("audit/problematic-audit/{id}/")
    fun patchSubmitProblematicAudit(@Path("id") audit_id : Int, @Body submitAuditRequest: SubmitAuditRequest): Call<Unit>
}