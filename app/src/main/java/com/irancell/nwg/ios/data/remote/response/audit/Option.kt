package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Option(
    @Expose
    @SerializedName("key")
    override var id: String,
    @Expose
    @SerializedName("name")
    var name : String,
    @Expose
    @SerializedName("value")
    var value: Double
) : BaseItem() ,  Parcelable {
    override fun toString(): String {
        return "Option(key=$id, name='$name', value=$value)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Option

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}

