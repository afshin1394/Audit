package com.irancell.nwg.ios.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SessionRequest(
    @SerializedName("username")
    @Expose
    private val username : String,
    @SerializedName("password")
    @Expose
    private val password : String)
