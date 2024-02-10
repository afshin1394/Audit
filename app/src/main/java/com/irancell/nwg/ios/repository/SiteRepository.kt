package com.irancell.nwg.ios.repository

import AsyncResult
import android.util.Log
import com.google.gson.Gson
import com.irancell.nwg.ios.data.local.asDomainModel
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.model.SiteState
import com.irancell.nwg.ios.data.remote.response.Site
import com.irancell.nwg.ios.data.remote.response.asDatabaseModel

import com.irancell.nwg.ios.database.dao.SiteDao
import com.irancell.nwg.ios.network.get.SiteService
import com.irancell.nwg.ios.presentation.audit.activity.siteDomain
import com.irancell.nwg.ios.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SiteRepository @Inject constructor(
    private val siteService: SiteService,
    private val siteDao: SiteDao
) {

     fun getSitesByProjectId(projectId: Int): Flow<AsyncResult<List<SiteDomain>>> {
        return dbTransaction(  { siteDao.getSites(projectId)  }, {
           it.asDomainModel()
        })

    }
   suspend fun getSitesByProjectIdCoroutine(projectId: Int): List<SiteDomain> {
        return withContext(Dispatchers.IO){
            val data = siteDao.getSites(projectId)
            data.asDomainModel()
        }

    }

    fun fetchSitesByProjectId(projectId: List<Int>): Flow<AsyncResult<ArrayList<Site>>> {
        return getSites(isOnline = true, projectId)
    }

    fun getSiteStateSituation(): Flow<List<SiteState>> {
        return siteDao.getSiteStateSituation()
    }

    private fun getSites(isOnline: Boolean? = true, projectId: List<Int>)
            : Flow<AsyncResult<ArrayList<Site>>> =
        flow {
            if (isOnline!!) {
                emit(AsyncResult.Loading(null, true))
                try {
                    var responses: ArrayList<Site> = arrayListOf()
                    siteDao.deleteAllSites()
                    if (projectId.isNotEmpty()) {
                        projectId.forEachIndexed { index, c ->
                            val call = siteService.getSites(c)
                            val response = call.execute()
                            Log.i(
                                "request",
                                "apiCall: session" + call.request().header("Authorization")
                            )
                            Log.i("request", "apiCall: " + call.request())
                            Log.i("request", "apiCall: " + call.request().url)
                            Log.i("responsebody", "apiCall: " + response.body())
                            if (response.isSuccessful) {
                                Log.i("onResponse", "successful")
                                response.body()?.also {
                                    responses.addAll(it.results)
                                    siteDao.insertAllSites(it.results.asDatabaseModel(c))
                                }
                            } else {
                                Log.i("onResponse", "error" + Gson().toJson(response.errorBody()))
                                emit(
                                    AsyncResult.Error(
                                        response.errorBody().toString(),
                                        response.code().toString()
                                    )
                                )
                            }

                            if (index == projectId.size - 1) {
                                Log.i("onResponse", "complete" + responses)
                                emit(AsyncResult.Success(responses, response.code().toString()))
                            }


                        }
                    } else {
                        emit(AsyncResult.Success(responses, "200"))
                    }
                } catch (exception: java.lang.Exception) {
                    emit(AsyncResult.Error(exception.message.toString(), ""))
                }


            } else {
                emit(AsyncResult.Error("NETWORK_UNREACHABLE", NETWORK_UNREACHABLE))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun updateSiteStateByAuditId(auditId: Int, state: Int) {
        withContext(Dispatchers.IO) {
            siteDao.updateSiteStateByAuditId(auditId, state)
        }
    }

    fun getAuditState(siteId: Int): Flow<AsyncResult<Int>> {
        return dbTransaction(transaction = { siteDao.getAuditState(siteId) }, mapper = { it })
    }
}