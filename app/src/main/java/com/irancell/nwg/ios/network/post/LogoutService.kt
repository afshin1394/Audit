package com.irancell.nwg.ios.network.post

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface LogoutService {
    @GET("auth/token/logout/")
    fun logout(): Call<Unit>
}