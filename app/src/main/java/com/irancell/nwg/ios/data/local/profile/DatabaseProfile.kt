package com.irancell.nwg.ios.data.local.profile

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseProfile(
    @PrimaryKey
    val id: Int,

    val first_name: String? = null,

    val last_name: String? = null,

    val company: String? = null,

    val organization: String? = null,

    val national_id: String? = null,

    val phone_number: String? = null,
)
