package com.falcon.otpbymodiji.network

import com.falcon.otpbymodiji.model.ApiResponse
import com.falcon.otpbymodiji.model.OtpResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(
        @Query("mobile") mobile: String
    ): ApiResponse

    @POST("send_otp")
    suspend fun sendOtp(
        @Query("mobile") mobile: String,
        @Query("language") language: String = "en"
    ): OtpResponse
}
