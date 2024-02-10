package com.irancell.nwg.ios.util.telephonyManager

import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.util.Log


class MyPhoneStateListener( signalListener: SignalListener) : PhoneStateListener() {


    companion object {
        var signalSupport = 0
        var signalPower = "undefined"
    }

    var signalListener = signalListener

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        super.onSignalStrengthsChanged(signalStrength)
        signalSupport = signalStrength.gsmSignalStrength
        Log.d(javaClass.canonicalName, "------ gsm signal --> $signalSupport")
        if (signalSupport > 30) {
            Log.d(javaClass.canonicalName, "Signal GSM : Good")
            signalPower = Good
        } else if (signalSupport > 20 && signalSupport < 30) {
            Log.d(javaClass.canonicalName, "Signal GSM : Avarage")
            signalPower = Avarage
        } else if (signalSupport < 20 && signalSupport > 3) {
            Log.d(javaClass.canonicalName, "Signal GSM : Weak")
            signalPower = Weak

        } else if (signalSupport < 3) {
            Log.d(javaClass.canonicalName, "Signal GSM : Very weak")
            signalPower = Very_weak

        }
        signalListener.onChange(signalPower)
    }
}

interface SignalListener
{
   fun onChange(signalPower: String)
}