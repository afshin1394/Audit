package com.irancell.nwg.ios.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseException (
    @PrimaryKey(autoGenerate = true)
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
)

fun List<DatabaseException>.asDomainModel() : List<com.irancell.nwg.ios.data.model.Exception>
{
    return map {
        com.irancell.nwg.ios.data.model.Exception(
             id = it.id  , token = it.token , email = it.email, password = it.password, version = it.version, model = it.model, className = it.className , it.fileName , it.method , it.lineNumber,it.stackTrace,it.message
        )
    }
}


