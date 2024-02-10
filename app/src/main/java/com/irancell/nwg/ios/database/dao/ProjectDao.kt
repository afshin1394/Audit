package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.irancell.nwg.ios.data.local.profile.DatabaseRole
import com.irancell.nwg.ios.data.local.project.DatabaseProject
import com.irancell.nwg.ios.data.local.project.DatabaseProjectWithRegions
import com.irancell.nwg.ios.data.local.project.DatabaseRegion

@Dao
interface ProjectDao {


    @Query("select * from DatabaseProject")
    fun getAllProjects() : List<DatabaseProject>

    @Query("select projectId from DatabaseProject")
    fun getAllProjectIds() : List<Int>


    @Query("DELETE FROM DatabaseRegion")
    fun deleteAllRegion()

    @Query("DELETE FROM DatabaseProject")
    fun deleteAllProject()

    @Insert
    fun insertAllRegion (regions : List<DatabaseRegion>)

    @Insert
    fun insertAllProject (regions : List<DatabaseProject>)
}