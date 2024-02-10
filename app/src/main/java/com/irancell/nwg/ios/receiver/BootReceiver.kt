package com.irancell.nwg.ios.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.irancell.nwg.ios.service.SendGpsService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("BootReceiver", "onReceive: reboot received")
            val serviceIntent = Intent(
                context,
                SendGpsService::class.java
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            }else{
                context.startService(serviceIntent)
            }
        }
    }
}