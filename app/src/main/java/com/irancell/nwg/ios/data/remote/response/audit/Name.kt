package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Name(
    @SerializedName("name_fa")
    @Expose
    var persian: String? = null,
    @SerializedName("name_en")
    @Expose
    var english: String? = null
) : Parcelable{

}
