package com.irancell.nwg.ios.data.remote.response.audit

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Data(
    @Expose
    @SerializedName("detail")
    var details: Detail?

) : BaseItem(), Parcelable {

    @IgnoredOnParcel
    var contentUri : String? = null
    @IgnoredOnParcel
    var is_remote_e_tilt: Boolean? = null
    @IgnoredOnParcel
    var label_image: String? = null
    @IgnoredOnParcel
    var is_blocked: Boolean? = null
    @IgnoredOnParcel
    var is_edited: Boolean? = null
    @IgnoredOnParcel
    var is_closed: Boolean? = null
    @IgnoredOnParcel
    var Image: String? = null
    @IgnoredOnParcel
    var options: ArrayList<Option>? = null
    @IgnoredOnParcel
    var value: String? = null
    @IgnoredOnParcel
    override var id: String
        get() =
            id
        set(id) {
            this.id = id
        }


    override fun toString(): String {
        return "Data(Image=$Image, options=$options, value=$value, details=$details)"
    }


}
