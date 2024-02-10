package com.irancell.nwg.ios.data.local.project

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.irancell.nwg.ios.data.local.profile.DatabaseProfile
import com.irancell.nwg.ios.data.model.RegionDomain
import com.irancell.nwg.ios.data.remote.response.RegionResponse
import com.irancell.nwg.ios.data.remote.response.pojo.Region
import com.irancell.nwg.ios.database.dao.RegionDao

@Entity(foreignKeys =
[ForeignKey(
    entity = DatabaseProject::class,
    childColumns = ["project_Id"],
    parentColumns = ["projectId"],
    onDelete = ForeignKey.CASCADE,
    onUpdate = ForeignKey.CASCADE
)])
data class DatabaseRegion constructor(
    @PrimaryKey
    var regionId : Int = 0,
    var regionName : String ="",
    var project_Id : Int = 0
){

    override fun toString(): String {
        return "DatabaseRegion(regionId=$regionId, regionName='$regionName', project_Id=$project_Id)"
    }
}

fun List<DatabaseRegion>.asRemoteModel(): List<Region> {
    return map {
        Region(
            id = it.regionId, name = it.regionName
        )
    }
}

fun List<DatabaseRegion>.asDomainModel(): List<RegionDomain> {
    return map {
        RegionDomain(
            id = it.regionId, name = it.regionName
        )
    }
}
fun DatabaseRegion.asDomainModel(): RegionDomain {
      return  RegionDomain(
            id = this.regionId, name = this.regionName
        )
}


