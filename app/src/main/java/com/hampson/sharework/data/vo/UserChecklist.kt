package com.hampson.sharework.data.vo

data class UserChecklist(
    val id: Int?,
    val user_id: Int,
    val job_id: Int,
    val job_checklist_id: Int,
    val answer: Boolean
)