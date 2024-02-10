package com.irancell.nwg.ios.data.model.profile

import com.irancell.nwg.ios.data.local.profile.DatabaseUser

data class UserDomain(
    val userId: Int,

    val pk : String? = null,

    val username : String? = null,

    val email : String? = null,
)
fun DatabaseUser.asDomainModel() : UserDomain{
    return UserDomain(this.user_id,this.pk,this.username,this.email)
}