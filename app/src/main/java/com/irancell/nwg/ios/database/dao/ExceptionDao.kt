package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.irancell.nwg.ios.data.local.DatabaseException

@Dao
interface ExceptionDao {
    @Insert
    fun insertException (databaseException: DatabaseException):Long

    @Query("select * from DatabaseException")
    fun getAllExceptions() : List<DatabaseException>


    @Query("DELETE FROM DatabaseException")
    fun deleteAll()
}