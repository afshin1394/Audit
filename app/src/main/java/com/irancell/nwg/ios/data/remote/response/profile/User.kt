package com.irancell.nwg.ios.data.remote.response.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.profile.DatabaseUser
import com.irancell.nwg.ios.data.model.profile.UserDomain

data class User(
@Expose
@SerializedName("pk")
val pk : String?= null,
@Expose
@SerializedName("username")
val username : String?= null,
@Expose
@SerializedName("email")
val email : String?= null,
)
fun User.asLocalModel() : DatabaseUser {
    return DatabaseUser(pk = this.pk,username = this.username , email = this.email)
}
fun User.asDomainModel() : UserDomain {
    return UserDomain(0,pk = this.pk,username = this.username , email = this.email)
}