package com.irancell.nwg.ios.data.remote.request

import com.google.android.gms.vision.MultiProcessor
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import java.io.File

data class UploadAuditRequest(

    val audit_id: Int,

    @SerializedName("name")
    @Expose
     val name: String,
    @SerializedName("file")
    @Expose
    val file: MultipartBody.Part,

    @SerializedName("number_of_part")
    @Expose
     val number_of_part: Int,


    @SerializedName("section")
    @Expose
     val section: Int
)
