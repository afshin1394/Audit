package com.irancell.github.modules

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.database.room.DataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.components.SingletonComponent
import org.junit.Rule
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db")
    fun provideInMemoryDb(@ApplicationContext context: Context) : DataBase =
        Room.inMemoryDatabaseBuilder(
            context, DataBase::class.java
        ).allowMainThreadQueries()
            .build()

    @Provides
    @Named("gsonTest")
    fun provideGson() : Gson{
        return GsonBuilder().create()
    }
}