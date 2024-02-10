package com.irancell.nwg.ios.security

import android.os.Build
import java.io.File

 fun isEmulator(): Boolean {
        var isEmulator = (Build.MANUFACTURER.contains("Genymotion")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.toLowerCase().contains("droid4x")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.HARDWARE == "goldfish"
                || Build.HARDWARE == "vbox86"
                || Build.HARDWARE.toLowerCase().contains("nox")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.PRODUCT == "sdk"
                || Build.PRODUCT == "google_sdk"
                || Build.PRODUCT == "sdk_x86"
                || Build.PRODUCT == "vbox86p"
                || Build.PRODUCT.toLowerCase().contains("nox")
                || Build.BOARD.toLowerCase().contains("nox")
                || (Build.BRAND.startsWith("generic") &&    Build.DEVICE.startsWith("generic")))
        if (isEmulator)
            return true
        val arrayOfString = Array(17) { i -> i.toString() }
        arrayOfString[0] = "/dev/socket/genyd"
        arrayOfString[1] = "/dev/socket/baseband_genyd"
        arrayOfString[2] = "/dev/socket/qemud"
        arrayOfString[3] = "/dev/qemu_pipe"
        arrayOfString[4] = "ueventd.android_x86.rc"
        arrayOfString[5] = "x86.prop"
        arrayOfString[6] = "ueventd.ttVM_x86.rc"
        arrayOfString[7] = "init.ttVM_x86.rc"
        arrayOfString[8] = "fstab.ttVM_x86"
        arrayOfString[9] = "fstab.vbox86"
        arrayOfString[10] = "init.vbox86.rc"
        arrayOfString[11] = "ueventd.vbox86.rc"
        arrayOfString[12] = "fstab.andy"
        arrayOfString[13] = "ueventd.andy.rc"
        arrayOfString[14] = "fstab.nox"
        arrayOfString[15] = "init.nox.rc"
        arrayOfString[16] = "ueventd.nox.rc"
        for (pipe in arrayOfString) {
            val file = File(pipe)
            if (file.exists()) {
                return true
            }
        }

        return false
    }
