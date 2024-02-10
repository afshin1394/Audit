package com.irancell.nwg.ios.di

import android.content.Context
import android.content.SharedPreferences
import com.irancell.nwg.ios.util.SharedPref
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PrefModule {

    @Provides
    fun providePreference(@ApplicationContext appContext: Context,
    ) : SharedPreferences =
        appContext.getSharedPreferences("ios-prefs",Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideSharedPrefs(
        sharedPreferences: SharedPreferences
    ) : SharedPref =
        SharedPref(sharedPreferences)

}