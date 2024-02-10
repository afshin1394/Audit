package com.irancell.nwg.ios.data.remote.response.root

import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.base.BaseDataClass
import com.irancell.nwg.ios.data.remote.response.pojo.Project

data class ProjectResponse(
    @SerializedName("results")
    override var results: ArrayList<Project>
) : BaseDataClass<Project>()


