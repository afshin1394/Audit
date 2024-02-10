package com.irancell.nwg.ios.network.get

import com.irancell.nwg.ios.data.remote.response.SiteResponse
import retrofit2.Call
import retrofit2.http.*

interface SiteService {
    @GET("audit/rigger-projects/sites/{id}/")
    fun getSites(@Path("id") projectId : Int): Call<SiteResponse>
}