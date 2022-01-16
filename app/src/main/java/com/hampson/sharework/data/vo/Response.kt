package com.hampson.sharework.data.vo

import com.google.gson.annotations.SerializedName

data class Response(
    val page: Int,

    val payload: Payload?,
    val meta: Meta?,

    val status: Boolean,
    val message: String
)