package com.irancell.nwg.ios.database.room

import androidx.room.*
import com.irancell.nwg.ios.data.local.*
import com.irancell.nwg.ios.data.local.profile.DatabaseProfile
import com.irancell.nwg.ios.data.local.profile.DatabaseRole
import com.irancell.nwg.ios.data.local.profile.DatabaseUser
import com.irancell.nwg.ios.data.local.project.DatabaseProject
import com.irancell.nwg.ios.data.local.project.DatabaseRegion
import com.irancell.nwg.ios.database.dao.*

@Database(
    entities = [DatabaseAttributes::class, DataBaseAttributesSubmit::class,  DatabaseSite::class, DatabaseGenerateAttributes::class, DatabaseProfile::class, DatabaseRole::class, DatabaseUser::class , DatabaseException::class, DatabaseProject::class, DatabaseRegion::class , DatabaseAttributeProblematic::class],
    version = 1,
    exportSchema = false
)
abstract class DataBase : RoomDatabase() {
    abstract val auditFormDao: AuditFormDao
    abstract val regionDao: RegionDao
    abstract val siteDao: SiteDao
    abstract val generateAttributesDao: GenerateAttributeDao
    abstract val profileDao: ProfileDao
    abstract val exceptionDao : ExceptionDao
    abstract val projectDao : ProjectDao
    abstract val submitAttributeDao : SubmitAttributeDao
    abstract val problematicFormDao : ProblematicFormDao
}