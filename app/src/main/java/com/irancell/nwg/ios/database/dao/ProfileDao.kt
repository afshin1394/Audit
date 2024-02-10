package com.irancell.nwg.ios.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.irancell.nwg.ios.data.local.profile.DataBaseProfileWithRoles
import com.irancell.nwg.ios.data.local.profile.DatabaseProfile
import com.irancell.nwg.ios.data.local.profile.DatabaseRole
import com.irancell.nwg.ios.data.local.profile.DatabaseUser

@Dao
interface ProfileDao {
    @Insert
    fun insertAllRoles (roles : List<DatabaseRole>)

    @Insert
    fun insertUser (databaseUser: DatabaseUser)

    @Insert
    fun insertProfile (dataBaseProfile: DatabaseProfile)

    @Query("DELETE FROM DatabaseProfile")
    fun deleteAllProfile()
    @Query("DELETE FROM DatabaseRole")
    fun deleteAllRole()
    @Query("DELETE FROM DatabaseUser")
    fun deleteAllUser()

    @Query("select * from DatabaseProfile Join DatabaseRole on DatabaseRole.profile_id = DatabaseProfile.id Join DatabaseUser on DatabaseUser.profile_id = DatabaseProfile.id")
    fun getProfileWithUserAndAllRoles() : DataBaseProfileWithRoles
}