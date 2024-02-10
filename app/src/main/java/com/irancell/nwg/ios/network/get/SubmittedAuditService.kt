package com.irancell.nwg.ios.network.get

import com.irancell.nwg.ios.data.remote.response.AuditStateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface SubmittedAuditService {
    @GET("audit/submit-audit-project/{id}")
    fun getSubmittedAudit(@Path("id") audit_id : Int): Call<AuditStateResponse>
}