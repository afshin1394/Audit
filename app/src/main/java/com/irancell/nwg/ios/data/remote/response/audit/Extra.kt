package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Extra(
    @Expose
    @SerializedName("value")
    var value : String?,
    @Expose
    @SerializedName("Option")
    var options : ArrayList<Option>?

) : Parcelable
