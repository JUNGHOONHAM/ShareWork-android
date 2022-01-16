package com.hampson.sharework.data.vo

data class JobChecklists(
    val contents: String,
    val id: Int,
    val job_id: Int,
    val user_checklist: UserChecklist
)