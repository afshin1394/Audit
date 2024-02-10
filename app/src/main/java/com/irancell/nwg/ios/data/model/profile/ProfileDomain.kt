package com.irancell.nwg.ios.data.model.profile


data class ProfileDomain(
    var user : UserDomain?= null,

    var role : List<RoleDomain>?= null,

    var first_name: String?= null,

    var last_name: String?= null,

    var company: String?= null,

    var organization: String?= null,

    var national_id: String?= null,

    var phone_number: String?= null
)
