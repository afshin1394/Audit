package com.irancell.nwg.ios.presentation.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.setCustomKeys
import com.irancell.nwg.ios.databinding.ActivitySplashBinding
import com.irancell.nwg.ios.network.base.AsyncStatus.*
import com.irancell.nwg.ios.util.GpsUtil
import com.irancell.nwg.ios.util.HIGH
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.EMAIL
import com.irancell.nwg.ios.util.constants.ENGLISH
import com.irancell.nwg.ios.util.constants.PASSWORD
import com.irancell.nwg.ios.util.constants.SESSION_TOKEN
import com.irancell.nwg.ios.presentation.base.BaseActivity
import com.irancell.nwg.ios.presentation.login.LoginActivity
import com.irancell.nwg.ios.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>() {
    var isGpsEnable: Boolean = false


    @Inject
    lateinit var sharedPref: SharedPref

    @Inject
    @Named("Version")
    lateinit var version: String

    @Inject
    @Named("Model")
    lateinit var model: String


    @Inject
    lateinit var firebaseCrashlytics: FirebaseCrashlytics
    override fun inflateBiding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initViewModel(): SplashViewModel {
        val splashViewModel: SplashViewModel by viewModels()
        return splashViewModel
    }

    override fun initFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun initViews() {


        viewModel.setDefaultSetting(ENGLISH, HIGH)
        firebaseCrashlytics.setUserId(sharedPref.getString(EMAIL)!!)
        firebaseCrashlytics.setCustomKeys {
            key("Model", model) // String value
            key("version", version) // boolean value
            key("user-token", sharedPref.getString(SESSION_TOKEN)!!) // double value
            key("email", sharedPref.getString(EMAIL)!!) // float value
            key("password", sharedPref.getString(PASSWORD)!!) // int value
        }
        checkDeviceValidity()


    }


    private fun checkDeviceValidity() {
        checkAccess()
//        if (isRooted() || isEmulator()) {
//            firebaseCrashlytics.log("invalid device detected")
//            lifecycleScope.launch {
//                Toast.makeText(
//                    this@SplashActivity,
//                    getString(R.string.EntranceImpossible),
//                    Toast.LENGTH_LONG
//                ).show()
//                delay(3000)
//                exitProcess(0)
//            }
//        } else {
//            viewModel.setDefaultLanguage(ENGLISH)
//            checkAccess()
//        }
    }

    fun checkAccess() {


        val externalPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE




        val permissions: Array<String> = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            externalPermission
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.plus(Manifest.permission.SCHEDULE_EXACT_ALARM)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.plus(Manifest.permission.POST_NOTIFICATIONS)
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
           permissions.plus(Manifest.permission.QUERY_ALL_PACKAGES)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.plus(Manifest.permission.FOREGROUND_SERVICE)
        }

        checkPermissions(
            permissions
        ) {

            if (it.all { it.granted }) {

                GpsUtil(this@SplashActivity).turnGPSOn(1000, object : GpsUtil.onGpsListener {
                    @RequiresApi(Build.VERSION_CODES.P)
                    override fun gpsStatus(isGPSEnable: Boolean) {
                        isGpsEnable = isGPSEnable
                        if (isGPSEnable) {

//                            val telephonyManager =
//                                this@SplashActivity.getSystemService(TELEPHONY_SERVICE) as TelephonyManager

                                Log.i("SplashActivity", "gpsStatus: "+ sharedPref.getString(SESSION_TOKEN))
                                if (sharedPref.getString(SESSION_TOKEN).equals("")) {
                                    openLogin()
                                } else {
                                    openMain()
                                }


                        }
                    }
                })

            } else {
                checkAccess()
            }
        }
    }

    private fun openLogin() {
        startActivity(
            Intent(
                this@SplashActivity, LoginActivity::class.java
            )
        )
        this@SplashActivity.finish()
    }

    private fun openMain() {

        startActivity(
            Intent(
                this@SplashActivity, MainActivity::class.java
            )
        )
        this@SplashActivity.finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            if (resultCode == RESULT_CANCELED) {
                GpsUtil(this@SplashActivity).turnGPSOn(1000, object : GpsUtil.onGpsListener {
                    override fun gpsStatus(isGPSEnable: Boolean) {
                        isGpsEnable = isGPSEnable
                        checkAccess()
                    }
                })
            } else if (resultCode == RESULT_OK) {
                isGpsEnable = true
                checkAccess()
            }
        }
    }
}