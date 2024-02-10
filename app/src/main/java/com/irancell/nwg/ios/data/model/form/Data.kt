package com.irancell.nwg.ios.data.model.form

import android.os.Parcel
import android.os.Parcelable
import com.irancell.nwg.ios.data.remote.response.audit.BaseItem

data class Data( var details: Detail?): BaseItem(), Parcelable {
    var contentUri : String? = null
    var is_remote_e_tilt: Boolean? = null
    var label_image: String? = null
    var is_blocked: Boolean? = null
    var is_edited: Boolean? = null
    var is_closed: Boolean? = null
    var Image: String? = null
    var options: ArrayList<Option>? = null
    var value: String? = null
    override var id: String
        get() =
            id
        set(id) {
            this.id = id
        }

    constructor(parcel: Parcel) : this(parcel.readParcelable(Detail::class.java.classLoader)) {
        contentUri = parcel.readString()
        is_remote_e_tilt = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        label_image = parcel.readString()
        is_blocked = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        is_edited = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        is_closed = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        Image = parcel.readString()
        value = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(details, flags)
        parcel.writeString(contentUri)
        parcel.writeValue(is_remote_e_tilt)
        parcel.writeString(label_image)
        parcel.writeValue(is_blocked)
        parcel.writeValue(is_edited)
        parcel.writeValue(is_closed)
        parcel.writeString(Image)
        parcel.writeString(value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}
