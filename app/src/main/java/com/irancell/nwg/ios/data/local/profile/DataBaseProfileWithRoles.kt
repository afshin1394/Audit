package com.irancell.nwg.ios.data.local.profile

import androidx.room.Embedded
import androidx.room.Relation
import com.irancell.nwg.ios.data.model.profile.ProfileDomain
import com.irancell.nwg.ios.data.model.profile.asDomainModel

data class DataBaseProfileWithRoles(
    @Embedded
    var profile : DatabaseProfile,
    @Embedded
    var user : DatabaseUser,
    @Relation(entity = DatabaseRole::class,parentColumn = "id", entityColumn = "role_id")
    var roles : List<DatabaseRole>
)
fun DataBaseProfileWithRoles.asDomainModel() : ProfileDomain{
    return ProfileDomain(this.user.asDomainModel(),this.roles.asDomainModel(),this.profile.first_name,this.profile.last_name,this.profile.company,this.profile.organization,this.profile.national_id,this.profile.phone_number)
}

