package com.irancell.nwg.ios.data.model

import android.os.Parcelable
import com.irancell.nwg.ios.data.local.DatabaseException
import kotlinx.parcelize.Parcelize

@Parcelize
data class Exception(
    val id: Int,
    val token : String,
    val email : String,
    val password : String,
    val version : String,
    val model : String,
    val className : String,
    val fileName : String,
    val method : String,
    val lineNumber : String,
    val stackTrace : String,
    val message : String
) : Parcelable


fun Exception.asDatabaseModel() : DatabaseException
{
    return DatabaseException(0,this.token,this.email,this.password,this.version,this.model,this.className,this.fileName,this.method,this.lineNumber,this.stackTrace,this.message)
}
