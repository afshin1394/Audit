package com.irancell.nwg.ios.repository

import AsyncResult
import com.irancell.nwg.ios.data.local.profile.asDomainModel
import com.irancell.nwg.ios.data.model.profile.ProfileDomain
import com.irancell.nwg.ios.data.remote.response.profile.asLocalModel
import com.irancell.nwg.ios.database.dao.ProfileDao
import com.irancell.nwg.ios.network.get.ProfileService
import com.irancell.nwg.ios.util.apiCall
import com.irancell.nwg.ios.util.dbTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepository  @Inject constructor(
    private val profileService: ProfileService,
    private val isOnline : Boolean,
    private val profileDao : ProfileDao
){



     fun getProfileRemote() : Flow<AsyncResult<List<ProfileDomain>>> =
         apiCall(isOnline, preProcess =  {},
             networkApi =  {
                 profileDao.deleteAllProfile()
                 profileDao.deleteAllRole()
                 profileDao.deleteAllUser()
                 profileService.getProfile()
             },
          postProcess =  {
             profileDao.insertProfile(it.asLocalModel())
             profileDao.insertAllRoles(it.role!!.asLocalModel())
             profileDao.insertUser(it.user!!.asLocalModel())
             arrayListOf(profileDao.getProfileWithUserAndAllRoles().asDomainModel())
         })

    fun getProfileLocal() : Flow<AsyncResult<List<ProfileDomain>>> {
      return  dbTransaction ( { arrayListOf( profileDao.getProfileWithUserAndAllRoles().asDomainModel()) } , mapper = {it})

//      val transaction  = dbTransaction({profileObjectBox.query().build().find() } ,{ it.asDomainModel(objectBoxUser,objectBoxRole)})
//      profileObjectBox.query().close()
//      return  transaction

    }



}