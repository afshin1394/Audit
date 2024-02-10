package com.irancell.nwg.ios.data.model

import androidx.room.PrimaryKey
import com.irancell.nwg.ios.data.remote.response.pojo.Region

data class ProjectDomain(
    var id: Int,
    var name : String,
    var datetime : String,
    var company : String,
    var regions : List<RegionDomain> = arrayListOf()
)
