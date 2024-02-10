package com.irancell.nwg.ios.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.commons.net.ntp.NTPUDPClient
import org.apache.commons.net.ntp.TimeInfo
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*

fun Long.getDateFromMillis(dateFormat: SimpleDateFormat) : String{

  // Create a DateFormatter object for displaying date in specified format.

// Create a calendar object that will convert the date and time value in milliseconds to date.
  var calendar = Calendar.getInstance();
  calendar.setTimeInMillis(this);
  return dateFormat.format(calendar.getTime());
}

 fun getTimeFromServer(address: String) : Flow<DataState<TimeInfo>>{
  var index = 0;
  return flow<DataState<TimeInfo>>{
    val timeClient = NTPUDPClient()


    timeClient.getTime(InetAddress.getByName(address))
  }.flowOn(Dispatchers.IO)
}




