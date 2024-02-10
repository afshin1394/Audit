package com.irancell.nwg.ios.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.base.BaseDataClass
import com.irancell.nwg.ios.data.local.DatabaseSite
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.response.pojo.Project
import kotlinx.parcelize.Parcelize

@Parcelize
data class SiteResponse(
    @SerializedName("results")
    @Expose
    override var results: ArrayList<Site>


) : BaseDataClass<Site>(), Parcelable


