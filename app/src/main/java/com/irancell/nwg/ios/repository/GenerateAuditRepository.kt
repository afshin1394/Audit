package com.irancell.nwg.ios.repository

import AsyncResult
import com.irancell.nwg.ios.data.remote.response.generator.ItemResponse
import com.irancell.nwg.ios.data.remote.response.generator.asDatabaseModel
import com.irancell.nwg.ios.database.dao.GenerateAttributeDao
import com.irancell.nwg.ios.network.get.GenerateAuditService
import com.irancell.nwg.ios.util.apiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateAuditRepository  @Inject constructor(
    private val generateAuditService: GenerateAuditService,
    private val generateAttributeDao: GenerateAttributeDao,
    private val isOnline : Boolean
){
    fun fetchGenerateAudits() : Flow<AsyncResult<List<ItemResponse>>>
    {
     return  apiCall(isOnline , preProcess = {}, networkApi =  { generateAuditService.getGeneratorAudits()}) {
         generateAttributeDao.deleteAllGenerateAttributes()
         generateAttributeDao.insertAllGenerateAttributes(it.asDatabaseModel())
         it
     }
    }


}