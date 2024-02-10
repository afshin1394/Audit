package com.irancell.nwg.ios.util

import com.irancell.nwg.ios.data.remote.response.audit.BaseItem

fun<T: BaseItem> findIndex(list: ArrayList<T>?, uuid: String) : Int{
    for (i in list!!.indices){
        if (list[i].id == uuid)
            return i
    }
    return -1
}