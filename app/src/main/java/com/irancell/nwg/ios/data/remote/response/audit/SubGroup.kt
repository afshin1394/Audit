package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.remote.response.server.Type
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubGroup(
    @Expose
    @SerializedName("uuid")
    override var id: String,
    @Expose
    @SerializedName("name")
    var name: Name,
    @Expose
    @SerializedName("identifier_name")
    var identifier_name: String = "",
    @Expose
    @SerializedName("type")
    var type: Type,
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
    @Expose
    @SerializedName( "items")
    var element: ArrayList<AttrElement>?
): BaseItem() , Parcelable {

    @IgnoredOnParcel
    var isOpen : Boolean = false
    @IgnoredOnParcel
    var isGenerated : Boolean = false
    @IgnoredOnParcel
    var parent_id : String? = null

    var categoryId : Long? = null



    override fun toString(): String {


        return "Item(uuid=$id, name='$name', item=$element)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SubGroup

        if (id != other.id) return false
        if (name != other.name) return false
        if (element != other.element) return false
        if (isOpen != other.isOpen) return false
        if (isGenerated != other.isGenerated) return false
        if (parent_id != other.parent_id) return false
        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (element?.hashCode() ?: 0)
        result = 31 * result + isOpen.hashCode()
        result = 31 * result + isGenerated.hashCode()
        result = 31 * result + (parent_id?.hashCode() ?: 0)
        result = 31 * result + (categoryId?.hashCode() ?: 0)
        return result
    }


}
