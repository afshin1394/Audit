package com.irancell.nwg.ios.data.local.profile

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.irancell.nwg.ios.data.model.profile.RoleDomain

@Entity (foreignKeys =
[ForeignKey(
entity = DatabaseProfile::class,
childColumns = ["profile_id"],
parentColumns = ["id"],
onDelete = CASCADE,
onUpdate = CASCADE
)])
data class DatabaseRole(
    @PrimaryKey(autoGenerate = true)
    val role_id : Int = 0,
    val code : Int,
    val name : String? = null,
    val profile_id : Int = 0
)


fun List<DatabaseRole>.asDomainModel() : List<RoleDomain>
{
    return map{
        RoleDomain(it.code,it.name)
}

}
