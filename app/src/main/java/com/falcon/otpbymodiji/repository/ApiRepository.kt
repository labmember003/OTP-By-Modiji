package com.falcon.otpbymodiji.repository

import com.falcon.otpbymodiji.model.ApiResponse
import com.falcon.otpbymodiji.model.OtpResponse
import com.falcon.otpbymodiji.network.ApiService
import javax.inject.Inject

class ApiRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun register(mobile: String): ApiResponse {
        return apiService.register(mobile)
    }

    suspend fun sendOtp(mobile: String): OtpResponse {
        return apiService.sendOtp(mobile)
    }
}
