package com.irancell.nwg.ios.network.get

import com.irancell.nwg.ios.data.remote.response.profile.ProfileResponse
import com.irancell.nwg.ios.data.remote.response.root.ProjectResponse
import retrofit2.Call
import retrofit2.http.GET

interface ProjectService {
    @GET("audit/rigger-projects/")
    fun getProjects(): Call<ProjectResponse>

}