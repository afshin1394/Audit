package com.irancell.nwg.ios.data.local.project

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.data.remote.response.pojo.Project
import com.irancell.nwg.ios.data.remote.response.pojo.Region

@Entity
data class DatabaseProject(
    @PrimaryKey
     var projectId: Int = 0,
     var name : String = "",
     var datetime : String = "",
     var company : String = "",
    ){
    override fun toString(): String {
        return "DatabaseProject(projectId=$projectId, name='$name', datetime='$datetime', company='$company')"
    }
}
fun List<DatabaseProject>.asRemoteModel( regions : ArrayList<Region>): List<Project> {
    return map {
        Project(
           id = it.projectId , name = it.name , company = it.company , datetime = it.datetime
        )
    }
}

fun List<DatabaseProject>.asDomainModel(): List<ProjectDomain> {
    return map {
        ProjectDomain(
            id = it.projectId , name = it.name , company = it.company , datetime = it.datetime
        )
    }
}

