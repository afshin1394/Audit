package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.irancell.nwg.ios.data.local.DataBaseAttributesSubmit
import com.irancell.nwg.ios.data.local.DatabaseAttributes

@Dao
interface SubmitAttributeDao {


    @Query("Update DataBaseAttributesSubmit set json = :json Where siteId = :siteId and groupId= :groupId")
    fun updateAttribute(siteId: Int,groupId: String,json: String)



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAttribute(databaseAttributesList : List<DataBaseAttributesSubmit>)

    @Query("Select * from DataBaseAttributesSubmit Where siteId = :siteId and parentGroupId = -1")
    fun getSiteMainForms(siteId: Int) : List<DatabaseAttributes>


    @Query("select * from DataBaseAttributesSubmit where siteId = :siteId ")
    fun getAllAttributesBaseOnSite(siteId: Int) : List<DatabaseAttributes>

    @Query("DELETE FROM DataBaseAttributesSubmit")
    fun deleteAll()

    @Query("select * from DataBaseAttributesSubmit where siteId = :siteId and groupId = :parentId")
    fun getAttributeByParentId(siteId: Int,parentId: String) : DatabaseAttributes

}