package com.irancell.nwg.ios.repository

import AsyncResult
import com.irancell.nwg.ios.data.local.DatabaseException
import com.irancell.nwg.ios.data.local.asDomainModel
import com.irancell.nwg.ios.database.dao.ExceptionDao
import com.irancell.nwg.ios.util.dbTransaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExceptionRepository @Inject constructor(
    private val exceptionDao: ExceptionDao
) {

    fun insertException(databaseException: DatabaseException): Flow<AsyncResult<Long>> {
        return dbTransaction( transaction =  {
            exceptionDao.insertException(databaseException)
        }, mapper = {it})
    }

    fun deleteAll(): Flow<AsyncResult<Unit>> {
        return dbTransaction( {
            exceptionDao.deleteAll()
        }, mapper = { })
    }

    fun getAll(): Flow<AsyncResult<List<com.irancell.nwg.ios.data.model.Exception>>> {
        return dbTransaction({
            exceptionDao.getAllExceptions()
        },
            {
                it.asDomainModel()
            }
        )
    }
}