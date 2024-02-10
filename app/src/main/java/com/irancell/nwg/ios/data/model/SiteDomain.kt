package com.irancell.nwg.ios.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SiteDomain(

    val siteId: Int,

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

    var audit_status: Int,

    val audit_priority: Int,

    val projectId: Int,

    val latitude: String,

    val longitude: String,

    var isOpen : Boolean,

    var audit_id : Int

) : Parcelable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SiteDomain

        if (siteId != other.siteId) return false
        if (site_name != other.site_name) return false
        if (technologies != other.technologies) return false
        if (region_name != other.region_name) return false
        if (province != other.province) return false
        if (vendor != other.vendor) return false
        if (contractor != other.contractor) return false
        if (site_type != other.site_type) return false
        if (tower_height != other.tower_height) return false
        if (site_installation_type != other.site_installation_type) return false
        if (site_status != other.site_status) return false
        if (audit_status != other.audit_status) return false
        if (audit_priority != other.audit_priority) return false
        if (projectId != other.projectId) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (isOpen != other.isOpen) return false
        if (audit_id != other.audit_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = siteId
        result = 31 * result + site_name.hashCode()
        result = 31 * result + technologies.hashCode()
        result = 31 * result + region_name.hashCode()
        result = 31 * result + province.hashCode()
        result = 31 * result + vendor.hashCode()
        result = 31 * result + contractor.hashCode()
        result = 31 * result + site_type.hashCode()
        result = 31 * result + tower_height.hashCode()
        result = 31 * result + site_installation_type.hashCode()
        result = 31 * result + site_status.hashCode()
        result = 31 * result + audit_status
        result = 31 * result + audit_priority
        result = 31 * result + projectId
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + isOpen.hashCode()
        result = 31 * result + audit_id
        return result
    }
}

