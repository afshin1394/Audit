package com.irancell.nwg.ios.data.remote.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResendRequest(
@SerializedName("session_id")
@Expose
private val session_id : String
)
