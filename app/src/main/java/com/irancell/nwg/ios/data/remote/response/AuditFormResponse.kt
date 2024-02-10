package com.irancell.nwg.ios.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.base.BaseDataClass
import com.irancell.nwg.ios.data.remote.response.audit.Audit
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuditFormResponse(
    @Expose
    @SerializedName("results")
    override var results : ArrayList<Audit>

): BaseDataClass<Audit>(), Parcelable
