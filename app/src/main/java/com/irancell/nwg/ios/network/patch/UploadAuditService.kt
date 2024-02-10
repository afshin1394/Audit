package com.irancell.nwg.ios.network.patch

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.remote.request.UploadAuditRequest
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface UploadAuditService {

    @PATCH("audit/upload-file/{audit_id}/")
    @Multipart
    fun patchUploadAudit (
        @Path("audit_id") audit_id : Int,
        @Part("name") name : String,
        @Part file: MultipartBody.Part,
        @Part("number_of_part") number_of_part : Int,
        @Part("section") section : Int
    ): Call<Unit>

    @PATCH("audit/problematic/upload-file/{audit_id}/")
    @Multipart
    fun patchUploadProblematicAudit(
        @Path("audit_id") audit_id : Int,
        @Part("name") name : String,
        @Part file: MultipartBody.Part,
        @Part("number_of_part") number_of_part : Int,
        @Part("section") section : Int
    ): Call<Unit>
}