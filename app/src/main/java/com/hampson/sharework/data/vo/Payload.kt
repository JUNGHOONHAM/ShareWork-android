package com.hampson.sharework.data.vo

import com.google.gson.annotations.SerializedName

data class Payload(
    val smsAuth: SmsAuth?,
    val user: User,

    val jobs: ArrayList<Job>
)