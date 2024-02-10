package com.irancell.nwg.ios.data.local.project

import androidx.room.Embedded
import androidx.room.Relation
import com.irancell.nwg.ios.data.model.ProjectDomain

data class DatabaseProjectWithRegions(
    @Embedded
    var project: DatabaseProject,
    @Relation(entity = DatabaseRegion ::class,parentColumn = "projectId", entityColumn = "project_Id")
    var region : ArrayList<DatabaseRegion>
)
fun  List<DatabaseProjectWithRegions>.asDomainModel() : List<ProjectDomain> {
   return  map{
       ProjectDomain(it.project.projectId,it.project.name,it.project.datetime,it.project.company,it.region.asDomainModel())
   }

}

