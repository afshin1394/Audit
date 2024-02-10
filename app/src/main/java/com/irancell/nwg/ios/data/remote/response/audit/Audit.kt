package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Audit(
    @Expose
    @SerializedName("uuid")  var id: String,
    @Expose
    @SerializedName("items") val group: ArrayList<Group>,
    ) :  Parcelable {

    override fun toString(): String {
        return "Audit(uuid=$id, items=$group)"
    }


}


