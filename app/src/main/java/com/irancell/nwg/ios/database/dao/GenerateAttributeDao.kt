package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.irancell.nwg.ios.data.local.DatabaseGenerateAttributes
import com.irancell.nwg.ios.data.local.profile.DatabaseRole
import kotlinx.coroutines.flow.Flow

@Dao
interface GenerateAttributeDao {

    @Query("Select json from DataBaseGenerateAttributes Where UUID = :child_form")
    fun getByGenerateId(child_form:String) : Flow<String>
    @Query("DELETE FROM DatabaseGenerateAttributes")
    fun deleteAllGenerateAttributes()
    @Insert
    fun insertAllGenerateAttributes (generateAttributes : List<DatabaseGenerateAttributes>)

}