package com.irancell.nwg.ios.database.dao

import androidx.room.*
import com.irancell.nwg.ios.data.local.DatabaseAttributes
import com.irancell.nwg.ios.data.model.TabModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditFormDao {

    @Query("SELECT COALESCE(max(attrId),0)+1 FROM DatabaseAttributes")
    fun getNextAutoId():  Int
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun insertAttribute(databaseAttributes: DatabaseAttributes)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAttribute(databaseAttributesList : List<DatabaseAttributes>)
    @Query("select * from DatabaseAttributes where siteId = :siteId and groupId = :groupId")
    fun getSiteAttributesByGroupId(siteId : Int,groupId : String): Flow<DatabaseAttributes>

    @Query("select * from DatabaseAttributes where siteId = :siteId and groupId = :groupId")
    fun getSiteAttributesByGroupIdSync(siteId : Int,groupId : Int): DatabaseAttributes
    @Query("select siteId ,groupId ,englishName , COALESCE(persianName, '') as persianName  from DatabaseAttributes where siteId = :siteId and typeId = :typeId")
    fun getSiteGroups(siteId : Int,typeId: Int): Flow<List<TabModel>>

    @Query("Update DatabaseAttributes set json = :json Where siteId = :siteId and groupId= :groupId ")
    fun updateAttribute(siteId: Int,groupId: String,json: String)
    @Query("Select json from DatabaseAttributes Where siteId = :siteId and groupId= :groupId ")
    fun getAttribute(siteId: Int,groupId: String) : String

    @Query("Select * from DatabaseAttributes Where siteId = :siteId and groupId= :groupId ")
    fun getAttributeWithFeatures(siteId: Int,groupId: String) : DatabaseAttributes
    @Query("DELETE FROM DatabaseAttributes WHERE siteId = :siteId and groupId = :groupId and typeId = :type")
    fun removeAttribute(siteId: Int, groupId: Int, type: Int)
    @Query("DELETE FROM DatabaseAttributes WHERE siteId = :siteId and groupId in (:groupIds) and typeId = :type")
    fun removeAttributes(siteId: Int, groupIds: ArrayList<Int>, type: Int)

    @Query("DELETE FROM DatabaseAttributes WHERE siteId = :siteId and (parentGroupId = :groupId or  groupId = :groupId) and typeId = :type")
    fun removeAttributesWithChildren(siteId: Int,groupId : String,type: Int)


    @Query("DELETE FROM DatabaseAttributes")
    fun deleteAll()


    @Query("Select * from DatabaseAttributes Where siteId = :siteId and parentGroupId = -1")
    fun getSiteMainForms(siteId: Int) : List<DatabaseAttributes>

    @Transaction
    fun removeAttributesWithChildrenAndUpdateAttr(siteId: Int,groupId: String,mainGroupId:String,type:Int,json: String) {
        removeAttributesWithChildren(siteId,groupId,type)
        updateAttribute(siteId, mainGroupId, json)
    }
    @Transaction
    fun insertAndUpdateAttribute(siteId: Int,groupId: String,json: String,databaseAttributes: DatabaseAttributes) {
        updateAttribute(siteId, groupId, json)
        insertAttribute(databaseAttributes)
    }
    @Transaction
    fun insertAllAndUpdateAttribute(siteId: Int,groupId: String,json: String,databaseAttributesList:ArrayList<DatabaseAttributes>) {
        updateAttribute(siteId, groupId, json)
        insertAllAttribute(databaseAttributesList)
    }

    @Query("select * from DatabaseAttributes where siteId = :siteId ")
    fun getAllAttributesBaseOnSite(siteId: Int) : List<DatabaseAttributes>


    @Query("select distinct parentGroupId from DatabaseAttributes where siteId = :siteId" )
    fun getParentGroupIds(siteId : Int) : List<String>


}