package com.irancell.nwg.ios.data.model.form

import com.irancell.nwg.ios.data.remote.response.audit.BaseItem

data class Option(
                      override var id: String,
                      var name : String,
                      var value: Double) : BaseItem()
