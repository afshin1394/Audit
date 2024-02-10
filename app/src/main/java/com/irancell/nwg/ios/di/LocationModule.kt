package com.irancell.nwg.ios.di

import android.content.Context
import androidx.lifecycle.LiveData
import com.irancell.nwg.ios.service.Location
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object LocationModule {

    @Provides
    fun provideLocationLivedata(
        @ApplicationContext appContext: Context,
    ): LiveData<android.location.Location> =
        Location(appContext).getLocation()


}