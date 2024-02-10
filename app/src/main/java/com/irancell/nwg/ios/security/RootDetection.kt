package com.irancell.nwg.ios.security

import android.os.Build
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

fun isRooted() : Boolean {
    if (checkRootMethod1()){
        return true
    }

    if (checkRootMethod2()){
        return true
    }
    if (checkRootMethod3()){
        return true
    }

return false
}


private fun checkRootMethod1(): Boolean {
    val buildTags = Build.TAGS
    return buildTags != null && buildTags.contains("test-keys")
}

private fun checkRootMethod2() : Boolean {
    val arrayOfString = Array(13) { i -> i.toString() }
    arrayOfString[0] = "/system/app/Superuser/Superuser.apk"
    arrayOfString[1] = "/system/app/Superuser.apk"
    arrayOfString[2] = "/sbin/su"
    arrayOfString[3] = "/system/bin/su"
    arrayOfString[4] = "/system/xbin/su"
    arrayOfString[5] = "/data/local/xbin/su"
    arrayOfString[6] = "/data/local/bin/su"
    arrayOfString[7] = "/system/sd/xbin/su"
    arrayOfString[8] = "/system/bin/failsafe/su"
    arrayOfString[9] = "/data/local/su"
    arrayOfString[10] = "/su/bin/su"
    arrayOfString[11] = "re.robv.android.xposed.installer-1.apk"
    arrayOfString[12] = "/data/app/eu.chainfire.supersu-1/base.apk"


    for (element in arrayOfString){
        if (File(element).exists()){
            return true
        }
    }
    return false
}

private fun checkRootMethod3(): Boolean {
    var process: Process? = null
    return try {
        process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
        val buffer = BufferedReader(InputStreamReader(process.inputStream))
        buffer.readLine() != null
    } catch (t: Throwable) {
        false
    } finally {
        process?.destroy()
    }
}
