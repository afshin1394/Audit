package com.irancell.nwg.ios.network.get

import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.data.remote.response.profile.ProfileResponse
import retrofit2.Call
import retrofit2.http.GET

interface GenerateAuditService {
    @GET("forms/generator-audit/")
    fun getGeneratorAudits(): Call<List<ItemResponse>>
}