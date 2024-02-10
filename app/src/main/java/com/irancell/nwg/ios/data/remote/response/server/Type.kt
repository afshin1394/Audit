package com.irancell.nwg.ios.data.remote.response.server

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Type(
    @Expose
    @SerializedName("id")
    val id : Int,
    @Expose
    @SerializedName("name")
    val name : String,
    ) : Parcelable{

    override fun toString(): String {
        return "Type(id=$id, name='$name')"
    }
}


