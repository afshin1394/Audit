package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.irancell.nwg.ios.data.local.DatabaseAttributeProblematic
import com.irancell.nwg.ios.data.local.DatabaseAttributes
import com.irancell.nwg.ios.data.model.TabModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ProblematicFormDao {

    @Query("DELETE FROM DatabaseAttributeProblematic")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertProblematicAttribute(databaseAttributes: DatabaseAttributeProblematic)

    @Query("select siteId ,groupId ,englishName , COALESCE(persianName, '') as persianName  from DatabaseAttributeProblematic where siteId = :siteId and typeId = :typeId")
    fun getSiteGroups(siteId : Int,typeId: Int): Flow<List<TabModel>>

    @Query("Select json from DatabaseAttributeProblematic Where siteId = :siteId and groupId= :groupId ")
    fun getAttribute(siteId: Int,groupId: String) : String

    @Query("Update DatabaseAttributeProblematic set json = :json Where siteId = :siteId and groupId= :groupId ")
    fun updateAttribute(siteId: Int,groupId: String,json: String)

    @Query("select * from DatabaseAttributeProblematic where siteId = :siteId and groupId = :groupId")
    fun getSiteAttributesByGroupId(siteId : Int,groupId : String): Flow<DatabaseAttributeProblematic>

    @Query("select * from DatabaseAttributeProblematic where siteId = :siteId ")
    fun getAllAttributesBaseOnSite(siteId: Int) : List<DatabaseAttributeProblematic>


}