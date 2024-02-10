package com.irancell.nwg.ios.network.get

import com.irancell.nwg.ios.data.remote.response.AuditFormResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProblematicFormService {
    @GET("forms/problematic/")
    fun getProblematicForm(): Call<AuditFormResponse>
}