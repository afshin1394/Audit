package com.irancell.nwg.ios.data.remote.request.sumbit_audit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SubmitAuditRequest(
    @SerializedName("form_value")
    @Expose
    private val formValue : ArrayList<FormValue>,

    )
