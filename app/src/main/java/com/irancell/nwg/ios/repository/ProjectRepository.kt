package com.irancell.nwg.ios.repository

import AsyncResult
import com.irancell.nwg.ios.data.local.project.DatabaseRegion
import com.irancell.nwg.ios.data.local.project.asDomainModel
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.data.remote.response.pojo.asDatabaseModel
import com.irancell.nwg.ios.data.remote.response.pojo.asDomainModel
import com.irancell.nwg.ios.database.dao.ProjectDao
import com.irancell.nwg.ios.network.get.ProjectService
import com.irancell.nwg.ios.util.apiCall
import com.irancell.nwg.ios.util.dbTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val projectService: ProjectService,
) {


    fun getAllProjects(): Flow<AsyncResult<List<ProjectDomain>>> {
        return dbTransaction({
            projectDao.getAllProjects()
        },
            {
                it.asDomainModel()
            }
        )
    }

    fun fetchProjects(): Flow<AsyncResult<List<ProjectDomain>>> {
        return apiCall(true, preProcess = {}, { projectService.getProjects() }, postProcess = {
            projectDao.deleteAllRegion()
            projectDao.deleteAllProject()
            val regions: ArrayList<DatabaseRegion> = ArrayList<DatabaseRegion>()
            for (result in it.results) {
                for (region in result.regions) {
                    regions.add(region.asDatabaseModel(result.id))
                }
            }
            val hs = HashSet(regions)
            val noDuplicate = ArrayList(hs)

            projectDao.insertAllProject(it.results.asDatabaseModel())
            projectDao.insertAllRegion(noDuplicate)
            it.results.asDomainModel()
        })
    }


    fun getProjectIds(): Flow<AsyncResult<List<Int>>> {
        return dbTransaction( {
            projectDao.getAllProjectIds()
        }, mapper = { it } )
    }


}