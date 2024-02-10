package com.irancell.nwg.ios.util.factory

import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import kotlinx.coroutines.flow.Flow

interface AuditObject <T>{
   suspend fun  create(attrElement: AttrElement) : Flow<ArrayList<T>>
}