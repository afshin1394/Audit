package com.irancell.nwg.ios.util

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo

import android.content.pm.PackageManager
import android.util.Log


fun searchQueryOnGoogle(context: Context, query: String) {
    val intent = Intent(Intent.ACTION_WEB_SEARCH)
    intent.putExtra(SearchManager.QUERY, query) // query contains search string
    context.startActivity(intent)
}

fun doesPackageExisted(context: Context,targetPackage: String): Boolean {
    val packages: List<ApplicationInfo>
    val pm = context.packageManager
    packages = pm.getInstalledApplications(0)
    Log.i("installedPackages", "doesPackageExisted: "+packages)
    for (packageInfo in packages) {
        if (packageInfo.packageName == targetPackage) return true
    }
    return false
}