package com.irancell.nwg.ios.repository

import AsyncResult
import com.irancell.nwg.ios.data.remote.response.SessionResponse
import com.irancell.nwg.ios.data.remote.response.TokenResponse
import com.irancell.nwg.ios.data.remote.request.ResendRequest
import com.irancell.nwg.ios.data.remote.request.SessionRequest
import com.irancell.nwg.ios.data.remote.request.TokenRequest
import com.irancell.nwg.ios.network.post.LoginService
import com.irancell.nwg.ios.network.post.LogoutService
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.apiCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val loginService: LoginService,
    private val logoutService: LogoutService,
    private val isOnline: Boolean,
    private val sharedPref: SharedPref
) {
    fun requestSession(username: String, password: String): Flow<AsyncResult<SessionResponse>> {
        return apiCall(
            isOnline,
            preProcess = {},
            networkApi = { loginService.requestSession(SessionRequest(username, password)) },
            postProcess = { it })
    }

    fun requestToken(session_id: String, verifyCode: String): Flow<AsyncResult<TokenResponse>> {
        return apiCall(
            isOnline,
            preProcess = {},
            networkApi = { loginService.requestToken(TokenRequest(session_id, verifyCode)) },
            postProcess = { it })
    }

    fun resendVerifyCode(session_id: String): Flow<AsyncResult<Unit>> {

        return apiCall(
            isOnline,
            preProcess = {},
            networkApi = { loginService.resendVerifyCode(ResendRequest(session_id)) },
            postProcess = { })
    }

    fun logout(): Flow<AsyncResult<Unit>> {
        return apiCall(isOnline, preProcess = {}, networkApi = {logoutService.logout() }, postProcess = { })
    }

}