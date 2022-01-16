package com.hampson.sharework.data.vo

data class Job (
    val id: Int,
    val jobId: Int,
    val address1: String,
    val address2: String,
    val contents: String,
    val job_date: String,
    val start_date: String,
    val end_date: String,
    val job_title: String,
    val job_type: String,
    val lat: Double,
    val lng: Double,
    val pay: String,
    val pay_type: String,
    val personnel: String,
    val status: String,
    val profile_img: String,

    val job_applications: List<JobApplication>,
    val job_checklists: List<JobChecklists>,
    val job_provideds: List<JobProvided>,
    val tags: List<Tag>,

    val jobable: Jobable,
    val jobable_id: Int,
    val jobable_type: String,

    val total_pay: String,
    val total_time: Any,
    val total_work_pay: Any
)