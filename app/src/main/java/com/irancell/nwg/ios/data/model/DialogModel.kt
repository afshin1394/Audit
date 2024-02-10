package com.irancell.nwg.ios.data.model

import android.graphics.Bitmap
import android.os.Parcelable
import com.irancell.nwg.ios.data.remote.response.audit.Name
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogModel(val title : String =""
                       , val firstAction : String = ""
                       , val secondAction : String = ""
                       , val firstActionColor : String = ""
                       , val secondActionColor : String =""
                       , val Image : Bitmap? = null, val rawRes : Int? = null
                       , val list : List<Name>? = null) : Parcelable {

}


