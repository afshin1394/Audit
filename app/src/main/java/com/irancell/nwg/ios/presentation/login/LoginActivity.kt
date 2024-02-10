package com.irancell.nwg.ios.presentation.login

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.irancell.nwg.ios.databinding.ActivityLoginBinding
import com.irancell.nwg.ios.receiver.MySMSBroadcastReceiver
import com.irancell.nwg.ios.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginActivityViewModel>() {


    @Inject
    lateinit var smsBroadcastReceiver : MySMSBroadcastReceiver
    override fun inflateBiding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun initFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun initNavController(): NavController {
        return viewBinding.navHostLogin.getFragment<NavHostFragment>().navController
    }



    @SuppressLint("MissingPermission")
    override fun initViews() {
        Log.i("smsBradcastReceiver", "initViews: "+smsBroadcastReceiver)
        var intentFilter = IntentFilter()
        intentFilter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsBroadcastReceiver,intentFilter)
//        Blurry.with(this).capture(viewBinding.root).color(Color.argb(66, 255, 255, 0))


    }





    override fun initViewModel(): LoginActivityViewModel {
        return LoginActivityViewModel();
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsBroadcastReceiver)

    }


    override fun onActivityResult(resultCode: Int, data: Intent?) {
        super.onActivityResult(resultCode, data)
    }


}