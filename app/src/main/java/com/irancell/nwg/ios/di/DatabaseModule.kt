package com.irancell.nwg.ios.di

import android.content.Context
import androidx.room.Room
import com.irancell.nwg.ios.database.room.DataBase
import com.irancell.nwg.ios.database.dao.*
import com.irancell.nwg.ios.util.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {




    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext appContext: Context,
        sharedPref: SharedPref
    ): DataBase {
        val passphrase = getPassphrase(sharedPref) ?: savePassPhrase(sharedPref)
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            appContext,
            DataBase::class.java,
            "database"
        )
        .build()
    }

    @Provides
    fun provideAttributeDao(database: DataBase): AuditFormDao {
        return database.auditFormDao
    }

    @Provides
    fun provideRegionDao(database: DataBase): RegionDao {
        return database.regionDao
    }

    @Provides
    fun provideSiteDao(database: DataBase): SiteDao {
        return database.siteDao
    }
    @Provides
    fun provideGenerateAttributeDao(database: DataBase): GenerateAttributeDao {
        return database.generateAttributesDao
    }
    @Provides
    fun provideProfileDao(database: DataBase): ProfileDao {
        return database.profileDao
    }

    @Provides
    fun provideExceptionDao(database: DataBase): ExceptionDao {
        return database.exceptionDao
    }

    @Provides
    fun provideProjectDao(database: DataBase): ProjectDao {
        return database.projectDao
    }

    @Provides
    fun provideSubmitAttributeDao(database: DataBase): SubmitAttributeDao {
        return database.submitAttributeDao
    }

    @Provides
    fun provideProblematicFormDao(database: DataBase): ProblematicFormDao {
        return database.problematicFormDao
    }
}