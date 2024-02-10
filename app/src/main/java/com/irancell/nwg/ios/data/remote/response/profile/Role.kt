package com.irancell.nwg.ios.data.remote.response.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.profile.DatabaseRole
import com.irancell.nwg.ios.data.model.profile.RoleDomain

data class Role(
    @Expose
    @SerializedName("code")
    val code : Int,
    @Expose
    @SerializedName("name")
    val name : String?= null,

)
fun List<Role>.asLocalModel() : List<DatabaseRole>{
    return map {
        DatabaseRole(
            code = it.code , name = it.name
        )
    }

}
fun List<Role>.asDomainModel() : List<RoleDomain>{
    return map {
        RoleDomain(
            code = it.code , name = it.name
        )
    }

}
