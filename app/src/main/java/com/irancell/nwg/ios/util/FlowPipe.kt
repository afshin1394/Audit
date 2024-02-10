package com.irancell.nwg.ios.util

import AsyncResult
import android.util.Log
import io.sentry.Sentry


import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import retrofit2.Call
import java.util.concurrent.TimeUnit

const val FLOW_PIPE = "FLOW_PIPE"
const val NETWORK_UNREACHABLE = "452"
const val FORBIDDEN = "403"
const val UNAUTHORIZED = "401"
const val DATABASE_TRANSACTION_FAILED = "453"
const val DATABASE_TRANSACTION_OK = "252"




    fun <T, R, D> apiCall(
        isOnline: Boolean = true,
        preProcess: (() -> R),
        networkApi: ((R) -> Call<T>),
        postProcess: ((T) -> D)
    ): Flow<AsyncResult<D & Any>> = flow {
        if (isOnline) {
            emit(AsyncResult.Loading(null, true)) // 1. Loading State
            val request = preProcess.invoke()
            try {
                val call = networkApi.invoke(request)
                val t1 = System.currentTimeMillis()
                val response = call.execute()
                val t2 = System.currentTimeMillis()

                Log.i(FLOW_PIPE, "apiCall: " + response.code())
                Log.i(FLOW_PIPE, "apiCall: " + TimeUnit.MILLISECONDS.toSeconds(t2 - t1))
                Log.i(FLOW_PIPE, "apiCall: " + call.request().url)




                if (response.isSuccessful) {
                    Sentry.captureMessage(String.format("%1s: %2s  \n  %3s: %4s","responseCode : ",response.code() ,"response body : ",response.body()))
                        val domain = response.body()?.let {
                        postProcess.invoke(it)
                    }
                    emit(AsyncResult.Success(domain, response.code().toString()))

                } else {
                    Sentry.captureMessage(String.format("%1s: %2s  \n  %3s: %4s","responseCode : ",response.code() ,"response error :",response.message()))
                    emit(
                        AsyncResult.Error(
                            response.errorBody().toString(),
                            response.code().toString()
                        )
                    )
                }
            } catch (exception: java.lang.Exception) {
                exception.message?.let { Sentry.captureMessage(it) }
                Log.i(FLOW_PIPE, "apiCall: "+exception.message)
                emit(AsyncResult.Error(exception.message.toString(), ""))
            }
        } else {
            emit(AsyncResult.Error("NETWORK_UNREACHABLE", NETWORK_UNREACHABLE))
        }
    }.flowOn(Dispatchers.IO)


    fun <T, R> dbTransaction(transaction: () -> T, mapper: (T) -> R) = flow {

        emit(AsyncResult.Loading(null, true))
        try {
            emit(AsyncResult.Success(mapper.invoke(transaction.invoke()), DATABASE_TRANSACTION_OK))
        } catch (e: Exception) {
            emit(AsyncResult.Error(e.message.toString(), DATABASE_TRANSACTION_FAILED))
            Log.i(FLOW_PIPE, "dbTransaction: "+e.message)

        }

    }.flowOn(Dispatchers.IO)


