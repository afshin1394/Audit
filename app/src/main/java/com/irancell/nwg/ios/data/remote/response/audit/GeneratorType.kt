package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GeneratorType(
    @SerializedName("id")
    @Expose
    val id : Int,

    @SerializedName("name")
    @Expose
    val name : String
) : Parcelable {


    override fun toString(): String {
        return "GeneratorType(id=$id, name='$name')"
    }
}

