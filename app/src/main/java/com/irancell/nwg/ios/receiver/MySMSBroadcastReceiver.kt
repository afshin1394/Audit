package com.irancell.nwg.ios.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import androidx.annotation.CallSuper
import com.irancell.nwg.ios.eventbus.ReceiveMessage
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus


interface ISMSListener {
    fun onSuccess(message: String?)
    fun onError(Status : Int)
}


const val pdu_type = "pdus"


abstract class HiltBroadcastReceiver : BroadcastReceiver() {
    @CallSuper
    override fun onReceive(context: Context, intent: Intent) {}
}

@AndroidEntryPoint
class MySMSBroadcastReceiver : HiltBroadcastReceiver() {
    lateinit var  listener : ISMSListener




    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
       if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION)  {
            try {
                // Get the SMS message.
                val bundle = intent.extras
                val msgs: Array<SmsMessage?>
                var strMessage = ""
                val format = bundle!!.getString("format")
                // Retrieve the SMS message received.
                // Retrieve the SMS message received.
                val pdus =
                    bundle[pdu_type] as Array<Any>?

                if (pdus != null) {

                    // Fill the msgs array.
                    msgs = arrayOfNulls(pdus.size)
                    for (i in msgs.indices) {
                        // Check Android version and use appropriate createFromPdu.
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            // If Android version M or newer:
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, format)
                        } else {
                            // If Android version L or older:
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        }
                        // Build the message to show.
//                        strMessage += "SMS from " + msgs[i]!!.getOriginatingAddress()
                        strMessage += """ ${msgs[i]!!.messageBody}
"""


                        // Log and display the SMS message.

                    }

                    var receiveMessage = ReceiveMessage()
                    receiveMessage.smsContent = strMessage.substringAfter(":").trim()
                    if (strMessage.contains("IOS")) {
                        EventBus.getDefault().post(receiveMessage);
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    }


