package com.irancell.nwg.ios.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FireBaseModule {

    @Provides
    @Singleton
    fun provideFireBaseCrashlytics() : FirebaseCrashlytics =
        FirebaseCrashlytics.getInstance()

}