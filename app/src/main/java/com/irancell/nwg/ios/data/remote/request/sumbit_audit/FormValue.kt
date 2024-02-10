package com.irancell.nwg.ios.data.remote.request.sumbit_audit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.remote.response.audit.Group

data class FormValue(
    @SerializedName("form")
    @Expose
    private val formValue : String,
    @SerializedName("value")
    @Expose
    private val value : Group,
)

