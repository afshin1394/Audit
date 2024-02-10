package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.remote.response.server.Type
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttrElement(

    @Expose
    @SerializedName("uuid")
    override var id: String,
    @Expose
    @SerializedName("name")
    var name: Name,
    @Expose
    @SerializedName("type")
    var type: Type,
    @Expose
    @SerializedName("identifier_name")
    var identifier_name: String = "",
    @Expose
    @SerializedName("mandatory")
    var mandatory : Boolean,
    @Expose
    @SerializedName("index")
    var index: Int?,
    @Expose
    @SerializedName("active_form")
    var active_form : Boolean?,
    @Expose
    @SerializedName("generator_type")
    var generator_type : GeneratorType? = null,
    @Expose
    @SerializedName("child_form")
    var child_form : String?,
    @Expose
    @SerializedName("extra")
    var extra : Extra?,
    @Expose
    @SerializedName("data")
    var data : Data?,


) :BaseItem() , Parcelable {
    @Expose
    @SerializedName("items")
    var group: ArrayList<Group>? = null




    var isGenerated : Boolean = false
    var parentId : String? = null
    var categoryId : Long? = null




    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttrElement

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (data != other.data) return false
        if (group != other.group) return false

        if (isGenerated != other.isGenerated) return false
        if (parentId != other.parentId) return false
        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (group?.hashCode() ?: 0)

        result = 31 * result + isGenerated.hashCode()
        result = 31 * result + (parentId?.hashCode() ?: 0)
        result = 31 * result + (categoryId?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "AttrElement(id='$id', name=$name, type=$type, index=$index, active_form=$active_form, generator_type=$generator_type, child_form=$child_form, extra=$extra, data=$data, group=$group, isGenerated=$isGenerated, parentId=$parentId, categoryId=$categoryId , items=$group)"
    }


}
