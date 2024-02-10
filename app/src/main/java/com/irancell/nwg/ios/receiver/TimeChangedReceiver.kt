//package com.irancell.nwg.ios.receiver
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.location.Location
//import android.util.Log
//import androidx.lifecycle.asLiveData
//import com.irancell.nwg.ios.Application
//import com.irancell.nwg.ios.util.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import timber.log.Timber
//import java.text.SimpleDateFormat
//import javax.inject.Inject
//
//class TimeChangedReceiver  @Inject constructor (
//   private val isOnline : Boolean
//        ) : BroadcastReceiver() {
//    companion object {
//        private val TAG = TimeChangedReceiver::class.java.simpleName
//    }
//
//
//
//
//    override fun onReceive(context: Context, intent: Intent) {
//        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
//        if (isOnline) {
//            getTimeFromServer("0.asia.pool.ntp.org").asLiveData().observeForever {
//                when (it) {
//                    is DataState.Loading -> {
//
//                    }
//                    is DataState.Success -> {
//                      if (System.currentTimeMillis() == it.data.returnTime){
//                          Application.appState.time = System.currentTimeMillis()
//                      }else{
//                              Application.appState.time = it.data.returnTime
//                      }
//                    }
//                    is DataState.Error -> {
//                    }
//                }
//            }
//            Timber.tag(TAG).i("onReceive: timeHasChanged")
//        }
//
//        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
//        CoroutineScope(Dispatchers.Main).launch {
//            delay(500)
//            Log.i("CurrentTime", "onReceive: "+Application.appState.time.getDateFromMillis(sdf))
//
//        }
//    }
//}
