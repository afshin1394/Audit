package com.irancell.nwg.ios.data.model

import com.irancell.nwg.ios.data.remote.response.audit.Name
import com.irancell.nwg.ios.data.remote.response.server.Type

data class MandatoryField(val uuid: String, val name: Name, val type: Type)
