package com.irancell.nwg.ios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.response.Site
import com.irancell.nwg.ios.data.remote.response.SiteResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Entity
data class DatabaseSite constructor(
    @PrimaryKey(autoGenerate = true)
    val siteId: Int = 0,
    val site_name: String,
    val technologies: String,
    val region_name: String,
    val province: String,
    val vendor: String,
    val contractor: String,
    val site_type: String,
    val tower_height: String,
    val site_installation_type: String,
    val site_status: String,
    val audit_status: Int,
    val audit_priority: Int,
    val projectId: Int,
    val latitude: String,
    val longitude: String,
    val audit_id: Int
)

fun List<DatabaseSite>.asRemoteModel(): List<Site> {
    return map {
        Site(
            site_name = it.site_name,
            technologies = it.technologies,
            region_name = it.region_name,
            province = it.province,
            vendor = it.vendor,
            contractor = it.contractor,
            site_type = it.site_type,
            tower_height = it.tower_height.toDouble(),
            site_installation_type = it.site_installation_type,
            site_status = it.site_status,
            audit_status = it.audit_status,
            audit_priority = it.audit_priority,
            projectId = it.projectId,
            latitude = it.latitude.toDouble(),
            longitude = it.longitude.toDouble(),
            audit_id = it.audit_id
        )
    }
}

 fun List<DatabaseSite>.asDomainModel(): List<SiteDomain> {
       return  this.map {
             SiteDomain(
                 siteId = it.siteId,
                 site_name = it.site_name,
                 technologies = it.technologies,
                 region_name = it.region_name,
                 province = it.province,
                 vendor = it.vendor,
                 contractor = it.contractor,
                 site_type = it.site_type,
                 tower_height = it.tower_height,
                 site_installation_type = it.site_installation_type,
                 site_status = it.site_status,
                 audit_status = it.audit_status,
                 audit_priority = it.audit_priority,
                 projectId = it.projectId,
                 latitude = it.latitude,
                 longitude = it.longitude,
                 false,
                 audit_id = it.audit_id

             )
         }

}
