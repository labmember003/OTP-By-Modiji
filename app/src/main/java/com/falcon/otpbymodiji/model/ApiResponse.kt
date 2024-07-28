package com.falcon.otpbymodiji.model

data class ApiResponse(
    val status: Int? = null,
    val result: String? = null,
    val otp_verified_at: String? = null,
    val message: String? = null
)

data class OtpResponse(
    val transaction_id: String? = null,
    val status: String? = null,
    val error: String? = null
)
