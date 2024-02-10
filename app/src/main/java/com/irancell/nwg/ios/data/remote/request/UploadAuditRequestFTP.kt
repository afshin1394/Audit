package com.irancell.nwg.ios.data.remote.request

import java.io.File

data class UploadAuditRequestFTP(val serverUrl : String,
                                 val serverUserName : String,
                                 val serverPassword : String,
                                 val folder : File,
                                 val regionName: String,
                                 val siteCode  : String)
