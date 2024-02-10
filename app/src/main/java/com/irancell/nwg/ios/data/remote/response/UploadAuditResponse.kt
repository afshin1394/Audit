package com.irancell.nwg.ios.data.remote.response

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

data class UploadAuditResponse(
    val progress: Int,
    val success: Boolean,
    val error: String,
    val type : Int
)
