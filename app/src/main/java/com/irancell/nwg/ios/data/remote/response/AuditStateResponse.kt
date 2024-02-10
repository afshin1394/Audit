package com.irancell.nwg.ios.data.remote.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.irancell.nwg.ios.data.model.AuditStateDomain

data class AuditStateResponse(
    @SerializedName("id")
    @Expose
    val audit_id : Int,
    @SerializedName("state")
    @Expose
    val state : Int

)
fun AuditStateResponse.asDomainModel() : AuditStateDomain {
    return AuditStateDomain(this.state,this.audit_id)
}
