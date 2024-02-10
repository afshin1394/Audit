package com.irancell.nwg.ios.base

import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.UUID

abstract class BaseDataClass<T> {


    abstract var results : ArrayList<T>

}