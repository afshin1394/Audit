package com.irancell.nwg.ios.data.remote.response.pojo

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.project.DatabaseRegion
import com.irancell.nwg.ios.data.model.RegionDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class Region(
    @SerializedName("id")
    @Expose
    val id : Int,

    @SerializedName("name")
    @Expose
    val name : String
) : Parcelable{
    override fun toString(): String {
        return "Region(id=$id, name='$name')"
    }
}







fun List<Region>.asDomainModel() : List<RegionDomain>{
    return map {
        RegionDomain(it.id,it.name)
    }
}

fun Region.asDatabaseModel( projectId : Int) : DatabaseRegion {

    return  DatabaseRegion(this.id,this.name,projectId)
}
