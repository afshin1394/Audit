package com.irancell.nwg.ios.data.remote.response.profile

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.local.profile.DatabaseProfile

data class ProfileResponse(
    @Expose
    @SerializedName("user")
    val user : User?= null,
    @Expose
    @SerializedName("role")
    val role : List<Role>?= null,
    @Expose
    @SerializedName("first_name")
    val first_name: String?= null,
    @Expose
    @SerializedName("last_name")
    val last_name: String?= null,
    @Expose
    @SerializedName("company")
    val company: String?= null,
    @Expose
    @SerializedName("organization")
    val organization: String?= null,
    @Expose
    @SerializedName("national_id")
    val national_id: String?= null,
    @Expose
    @SerializedName("phone_number")
    val phone_number: String?= null,
)
fun ProfileResponse.asLocalModel() : DatabaseProfile {
    return DatabaseProfile( 0,first_name = this.first_name,last_name = this.last_name,company = this.company, organization = this.organization, national_id = this.national_id,phone_number=this.phone_number)
}

