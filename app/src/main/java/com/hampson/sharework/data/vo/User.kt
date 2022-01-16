package com.hampson.sharework.data.vo

data class User(
    val id: Int,
    val phoneNumber: String,
    val email: String,
    val comment: String?,
    val name: String,
    val profileImg: String,
    val residentNumber: ResidentNumber,
    val uid: String?,
    val userType: String
)

data class ResidentNumber(
    val front: String,
    val rear: String
)

data class UserRequest(
    val email: String,
    val name: String,
    val phoneNumber: String,
    val residentNumberFront: String,
    val residentNumberRear: String
)