package com.irancell.nwg.ios.data.remote.response.pojo

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.project.DatabaseProject
import com.irancell.nwg.ios.data.local.project.DatabaseRegion
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.data.model.RegionDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class Project(
    @SerializedName("id")
    @Expose
    val id : Int,
    @SerializedName("name")
    @Expose
    val name : String,
    @SerializedName("company")
    @Expose
    val company : String,
    @SerializedName("datetime")
    @Expose
    val datetime : String,
    @SerializedName("region")
    @Expose
    val regions : ArrayList<Region> = arrayListOf()

) : Parcelable{
    override fun toString(): String {
        return "Project(id=$id, name='$name', company='$company', datetime='$datetime', regions=$regions)"
    }
}

fun List<Project>.asDatabaseModel() : List<DatabaseProject>
{
    return map{
        DatabaseProject(it.id, it.name.uppercase(),it.datetime,it.company)
    }
}

fun List<Project>.asDomainModel() : List<ProjectDomain>
{
    return map{
        ProjectDomain(it.id, it.name,it.datetime,it.company, ArrayList( it.regions.asDomainModel()))
    }
}
