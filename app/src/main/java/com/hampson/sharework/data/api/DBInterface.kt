package com.hampson.sharework.data.api

import com.hampson.sharework.data.vo.*
import io.reactivex.Single
import retrofit2.http.*

interface DBInterface {

    // sms_auth
    @POST("api/v3/sms_auth/send_sms")
    fun sendPhoneNumber(@Body smsAuthRequest: SmsAuthRequest): Single<Response>

    @POST("api/v3/sms_auth/verified")
    fun sendVerifiedNumber(@Body smsAuthRequest: SmsAuthRequest): Single<Response>


    // user
    @POST("api/v3/registration")
    fun signUp(@Body userRequest: UserRequest): Single<Response>


    // job
    @GET("api/v3/Job")
    fun getJobs(@Query("northeastLat") northeastLat: Double, @Query("northeastLng") northeastLng: Double,
                @Query("southwestLat") southwestLat: Double, @Query("southwestLng") southwestLng: Double): Single<Response>

    @GET("api/v3/Job/Cluster")
    fun getJobIds(@Query("jobIds") jobIds: ArrayList<Int>, @Query("page") page: Int, @Query("pageSize") pageSize: Int,
                  @Query("userLat") userLat: Double, @Query("userLng") userLng: Double): Single<Response>
}