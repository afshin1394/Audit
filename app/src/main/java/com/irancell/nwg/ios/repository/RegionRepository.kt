package com.irancell.nwg.ios.repository

import com.irancell.nwg.ios.data.local.asRemoteModel
import com.irancell.nwg.ios.data.remote.response.RegionResponse
import com.irancell.nwg.ios.database.dao.RegionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegionRepository @Inject constructor(
    private val regionDao: RegionDao
){

//    fun getAllRegions() : Flow<List<RegionResponse>> =
//        regionDao.getRegions().map {
//            it.asRemoteModel()
//        }
}