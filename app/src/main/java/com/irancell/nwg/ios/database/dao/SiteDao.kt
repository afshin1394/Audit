package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.data.local.project.DatabaseProject
import com.irancell.nwg.ios.data.model.SiteState
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @Query("select * from DatabaseSite where projectId = :project_id")
    fun getSites(project_id: Int): List<DatabaseSite>
    @Query("select * from DatabaseSite where projectId = :project_id and audit_status in (:status)")
    fun getSites(project_id: Int , status : Array<Int> ): List<DatabaseSite>
    @Query("select * from DatabaseSite")
    fun getAllSites(): List<DatabaseSite>
    @Query("DELETE FROM DatabaseSite")
    fun deleteAllSites()
    @Insert
    fun insertAllSites (sites : List<DatabaseSite>)

//    @Query("UPDATE DatabaseSite SET audit_status = :state WHERE siteId = :siteId")
//    fun updateSiteState(siteId : Int,state : Int )

    @Query("select count(*) as count,audit_status from DataBaseSite group by audit_status")
    fun getSiteStateSituation() : Flow<List<SiteState>>

    @Query("update DatabaseSite set audit_status = :state where audit_id = :auditId")
    fun updateSiteStateByAuditId(auditId: Int, state: Int)


    @Query("select audit_status from DatabaseSite where siteId =:siteId")
    fun getAuditState(siteId : Int) : Int
}