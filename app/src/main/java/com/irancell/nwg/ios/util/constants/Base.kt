package com.irancell.nwg.ios.util.constants

import android.content.Context
import com.google.gson.JsonObject
import com.irancell.nwg.ios.util.getState
import java.io.File

fun auditImageFolder(context: Context) : File {
   val folder = File(context.filesDir, "AuditImages")
    if (!folder.exists())
        folder.mkdir()

    return folder
}

fun auditRegionFolder(context: Context,regionName : String) {
    val folder = File(context.filesDir.path +"/AuditImages/" , regionName)
    if (!folder.exists())
        folder.mkdir()

}

fun auditSiteFolder(context: Context,regionName : String,siteCode : String) {
    val folder = File(context.filesDir.path +"/AuditImages/" + regionName+"/", siteCode)
    if (!folder.exists())
        folder.mkdir()

}
fun auditImageSubFolder(context: Context,regionName : String , siteCode : String,groupName: String) : File {
    val folder = File(context.filesDir.path+"/AuditImages/"+regionName+"/"+siteCode+"/", groupName)
    if (!folder.exists())
        folder.mkdir()

    return folder
}

fun auditGroupFolder(context: Context,regionName: String,siteCode: String) : File{
    var i = 0
    var folder : File? = null
    val path = context.filesDir.path + "/AuditImages/" + regionName + "/" + siteCode + "/"
    folder = createFolder(path)

//    while (i < getState().size) {
//        folder = createFolder(path, getState()[i])
//        path+=getState()[i]+"/"
//        i++
//    }
    return folder!!
}

fun auditZipGroupFolder(fileDirPath : String,regionName: String,siteCode: String) : File{
    var i = 0
    var folder : File? = null
    val path = "$fileDirPath/AuditImagesZip/$regionName/$siteCode/"
    createFolder("$fileDirPath/AuditImagesZip/")
    createFolder("$fileDirPath/AuditImagesZip/$regionName/")
    folder = createFolder(path)


//    while (i < getState().size) {
//        folder = createFolder(path, getState()[i])
//        path+=getState()[i]+"/"
//        i++
//    }
    return folder!!
}



fun createFolder(parent : String, s : String = "") : File{
    val folder = File(parent , s)
    if (!folder.exists())
        folder.mkdir()

    return folder
}