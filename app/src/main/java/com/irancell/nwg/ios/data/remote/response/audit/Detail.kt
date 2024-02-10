package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Detail(
    @Expose
    @SerializedName("date")
    var date: String? ,

    @Expose
    @SerializedName("latitude")
    var latitude : String?,

    @Expose
    @SerializedName("longitude")
    var longitude : String?) : Parcelable
