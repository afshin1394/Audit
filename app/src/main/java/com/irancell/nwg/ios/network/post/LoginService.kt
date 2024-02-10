package com.irancell.nwg.ios.network.post


import com.irancell.nwg.ios.data.remote.response.ResendResponse
import com.irancell.nwg.ios.data.remote.response.TokenResponse
import com.irancell.nwg.ios.data.remote.response.SessionResponse
import com.irancell.nwg.ios.data.remote.request.ResendRequest
import com.irancell.nwg.ios.data.remote.request.SessionRequest
import com.irancell.nwg.ios.data.remote.request.TokenRequest
import retrofit2.Call
import retrofit2.http.*

interface LoginService {


    @POST("auth/token/2fa/login/")
    fun requestSession(@Body sessionRequest: SessionRequest): Call<SessionResponse>

    @POST("auth/token/2fa/verify/")
    fun requestToken(@Body tokenRequest: TokenRequest): Call<TokenResponse>


    @POST("auth/token/2fa/resend/")
    fun resendVerifyCode(@Body resendRequest: ResendRequest): Call<Unit>



}