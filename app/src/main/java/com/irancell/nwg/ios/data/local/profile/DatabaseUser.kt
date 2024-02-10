package com.irancell.nwg.ios.data.local.profile

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity( foreignKeys =
[ForeignKey(
    entity = DatabaseProfile::class,
    childColumns = ["profile_id"],
    parentColumns = ["id"],
    onDelete = CASCADE,
    onUpdate = CASCADE
)])
data class DatabaseUser(
    @PrimaryKey
    var user_id: Int = 0,

    var pk: String?=null,

    var username: String?=null,

    var email: String?=null,

    var profile_id : Int = 0
)