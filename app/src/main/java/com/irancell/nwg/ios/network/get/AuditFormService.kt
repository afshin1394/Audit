package com.irancell.nwg.ios.network.get

import com.irancell.nwg.ios.data.remote.response.AuditFormResponse
import com.irancell.nwg.ios.data.remote.response.SiteResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface AuditFormService {
    @GET("forms/initial-audit/")
    fun getAuditForm(): Call<AuditFormResponse>
}