package com.irancell.nwg.ios.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.response.SiteResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class Site(

    @SerializedName("site_name")
    @Expose
    val site_name: String,
    @SerializedName("technologies")
    @Expose
    val technologies: String,
    @SerializedName("region")
    @Expose
    val region_name: String,
    @SerializedName("province")
    @Expose
    val province: String,
    @SerializedName("vendor")
    @Expose
    val vendor: String,
    @SerializedName("contractor")
    @Expose
    val contractor: String,
    @SerializedName("site_type")
    @Expose
    val site_type: String,
    @SerializedName("tower_height")
    @Expose
    val tower_height: Double,
    @SerializedName("site_installation_type")
    @Expose
    val site_installation_type: String,

    @SerializedName("site_status")
    @Expose
    val site_status: String,
    @SerializedName("audit_status")
    @Expose
    val audit_status: Int,
    @SerializedName("audit_priority")
    @Expose
    val audit_priority: Int,
    @SerializedName("projectId")
    @Expose
    val projectId: Int,
    @SerializedName("latitude")
    @Expose
    val latitude: Double,
    @SerializedName("longitude")
    @Expose
    val longitude: Double,
    @SerializedName("audit_id")
    @Expose
    val audit_id : Int

):  Parcelable


fun List<Site>.asDatabaseModel(projectId: Int): List<DatabaseSite> {
    return map {
        DatabaseSite(
            site_name = it.site_name,
            technologies = it.technologies,
            region_name = it.region_name,
            province = it.province,
            vendor = it.vendor,
            contractor = it.contractor,
            site_type = it.site_type,
            tower_height = it.tower_height.toString(),
            site_installation_type = it.site_installation_type,
            site_status = it.site_status,
            audit_status = it.audit_status,
            audit_priority = it.audit_priority,
            projectId = projectId,
            latitude =  it.latitude.toString(),
            longitude = it.longitude.toString(),
            audit_id = it.audit_id
        )
    }
}
