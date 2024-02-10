package com.irancell.nwg.ios.di

import android.content.Context
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.irancell.nwg.ios.receiver.MySMSBroadcastReceiver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SMSRetrieverModule {
    @Provides
    @Singleton
    fun provideSmsRetriever(@ApplicationContext applicationContext: Context) : SmsRetrieverClient =
        SmsRetriever.getClient(applicationContext);

    @Provides
    @Singleton
    fun provideSmsListener() = MySMSBroadcastReceiver()


}