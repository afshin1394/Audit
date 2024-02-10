package com.irancell.nwg.ios

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorkerFactory
import com.google.gson.Gson
import com.gu.toolargetool.BuildConfig
import com.gu.toolargetool.TooLargeTool
import com.irancell.nwg.ios.presentation.splash.SplashActivity
import com.irancell.nwg.ios.repository.ExceptionRepository
import com.irancell.nwg.ios.repository.ProfileRepository
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.telephonyManager.MyPhoneStateListener
import com.irancell.nwg.ios.util.telephonyManager.SignalListener
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.HiltAndroidApp
import net.gotev.uploadservice.UploadServiceConfig
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import kotlin.system.exitProcess


const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
const val IMAGE_QUALITY = "ImageQuality"

@HiltAndroidApp
class Application : android.app.Application(), SignalListener,
    androidx.work.Configuration.Provider {
    var mTelephonyManager: TelephonyManager? = null
    var mPhoneStatelistener: MyPhoneStateListener? = null



    companion object {
        lateinit var shared: SharedPref
        const val notificationChannelID = "IOSChannel"
    }


    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var exceptionRepository: ExceptionRepository

    @Inject
    @Named("Version")
    lateinit var version: String

    @Inject
    @Named("Model")
    lateinit var model: String





    override fun onCreate() {
        super.onCreate()
        UploadServiceConfig.initialize(
            context = this,
            defaultNotificationChannel = notificationChannelID,
            debug = BuildConfig.DEBUG
        )
        shared = sharedPref
        mPhoneStatelistener = MyPhoneStateListener(this)
        mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    }





    override fun onChange(signalPower: String) {
        showToast(applicationContext, signalPower)
    }
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration(): androidx.work.Configuration {
     return   androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }


}