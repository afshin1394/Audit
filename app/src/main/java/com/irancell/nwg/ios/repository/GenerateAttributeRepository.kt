package com.irancell.nwg.ios.repository

import com.google.gson.Gson
import com.irancell.nwg.ios.database.dao.GenerateAttributeDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateAttributeRepository @Inject constructor(
    private val gson: Gson,
    private val generateAttributeDao: GenerateAttributeDao
){
    fun getByChildForm(childForm:String) : Flow<String> {
        return generateAttributeDao.getByGenerateId(childForm)
    }
}