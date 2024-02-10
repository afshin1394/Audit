package com.irancell.nwg.ios.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SessionResponse(@Expose
                           @SerializedName("session_id")
                           val session_id: String,

                           @Expose
                           @SerializedName("phone_number")
                           val phone_number: String?)

