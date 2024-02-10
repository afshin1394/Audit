package com.irancell.nwg.ios.util

import android.annotation.SuppressLint
import android.content.SharedPreferences
import javax.inject.Inject


class SharedPref  @Inject constructor(private val preferences: SharedPreferences) {

    fun putString( key: String,value : String) {
        val editor = preferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putLong(key: String,value: Long){
        val editor = preferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun putInt(key: String,value: Int){
        val editor = preferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putFloat(key: String,value: Float){
        val editor = preferences.edit()
        editor.putFloat(key, value)
        editor.apply()
    }


    fun getString(key: String,alternative: String = "") : String?{
      return  preferences.getString(key,alternative)
    }

    fun getLong(key: String) : Long{
        return  preferences.getLong(key,-1L)
    }

    fun getInt(key: String) : Int{
        return  preferences.getInt(key,-1)
    }

    fun getFloat(key: String) : Float{
        return  preferences.getFloat(key,-1f)
    }

    @SuppressLint("CommitPrefEdits")
    fun remove(key : String){
        val editor = preferences.edit()
        editor.remove(key)
        editor.apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun removeAll(){
        val editor = preferences.edit()
        editor.clear()
        editor.apply()

    }
}