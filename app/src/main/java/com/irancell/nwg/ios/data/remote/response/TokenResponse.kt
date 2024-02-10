package com.irancell.nwg.ios.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenResponse(@Expose
                         @SerializedName("auth_token")
                         val auth_token: String,
)