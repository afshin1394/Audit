package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.remote.response.server.Type
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    @Expose
    @SerializedName("uuid")
    override var id: String,
    @Expose
    @SerializedName("name")
    var name: Name,
    @Expose
    @SerializedName("identifier_name")
    var identifier_name: String? = "",

    @Expose
    @SerializedName("type")
    var type: Type,
    @Expose
    @SerializedName("index")
    var index: Int,
    @Expose
    @SerializedName("active_form")
    var active_form: Boolean,
    @Expose
    @SerializedName("generator_type")
    var generator_type: GeneratorType? = null,
    @Expose
    @SerializedName("child_form")
    var child_form: String?,
    @Expose
    @SerializedName("extra")
    var extra: Extra?,
    @Expose
    @SerializedName("data")
    var data: Data? = null,
    @Expose
    @SerializedName("items")
    var subGroups: ArrayList<SubGroup>,
) : BaseItem(),  Parcelable {

    override fun toString(): String {
        return "Item(uuid=$id, name='$name',items='$subGroups')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (id != other.id) return false
        if (name != other.name) return false
        if (subGroups != other.subGroups) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + subGroups.hashCode()
        return result
    }


}

