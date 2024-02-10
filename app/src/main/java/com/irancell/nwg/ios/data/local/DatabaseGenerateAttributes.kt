package com.irancell.nwg.ios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseGenerateAttributes constructor(
    @PrimaryKey
    val UUID : String,
    val name : String?=null,
    val json: String
)
