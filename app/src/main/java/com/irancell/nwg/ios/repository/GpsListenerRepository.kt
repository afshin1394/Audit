package com.irancell.nwg.ios.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GpsListenerRepository @Inject constructor(
    @ApplicationContext private val appContext: Context
) {

//    fun listenForGpsSignal() : LiveData<WorkInfo> {
//        val gpsWorker = PeriodicWorkRequestBuilder<GpsWorker>(15,TimeUnit.MINUTES)
//            .setId(UUID.randomUUID())
//            .build()
//        val workManager: WorkManager = WorkManager.getInstance(appContext)
//        workManager.enqueueUniquePeriodicWork("Listening For Gps Signal...", ExistingPeriodicWorkPolicy.UPDATE, gpsWorker)
//
//        return workManager.getWorkInfoByIdLiveData(gpsWorker.id)
//    }
}