package com.hampson.sharework.data.vo

data class SmsAuth(
    val token: String,
    val user: User?
)

data class SmsAuthRequest(
    val receiver: String?,
    val token: String?,
    val verifiedNumber: String?
)