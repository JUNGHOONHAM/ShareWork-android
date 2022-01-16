package com.hampson.sharework.ui.membership.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.repository.sms_auth.SendPhoneNumberNetworkDataSource
import com.hampson.sharework.data.repository.sms_auth.SendVerifiedNumberNetworkDataSource
import com.hampson.sharework.data.vo.Response
import com.hampson.sharework.data.vo.SmsAuth
import io.reactivex.disposables.CompositeDisposable

class SignInRepository (private val apiService : DBInterface)  {
    private lateinit var sendPhoneNumberNetworkDataSource: SendPhoneNumberNetworkDataSource
    private lateinit var sendVerifiedNumberNetworkDataSource: SendVerifiedNumberNetworkDataSource

    fun sendPhoneNumber (compositeDisposable: CompositeDisposable, phoneNumber: String) : MutableLiveData<Response> {
        sendPhoneNumberNetworkDataSource = SendPhoneNumberNetworkDataSource(apiService, compositeDisposable)
        sendPhoneNumberNetworkDataSource.sendPhoneNumber(phoneNumber)

        return sendPhoneNumberNetworkDataSource.downlodedResponse
    }

    fun sendVerifiedNumber (compositeDisposable: CompositeDisposable, phoneNumber: String, token: String, verifiedNumber: String) : MutableLiveData<Response> {
        sendVerifiedNumberNetworkDataSource = SendVerifiedNumberNetworkDataSource(apiService, compositeDisposable)
        sendVerifiedNumberNetworkDataSource.sendVerifiedNumber(phoneNumber, token, verifiedNumber)

        return sendVerifiedNumberNetworkDataSource.downlodedResponse
    }

    fun sendPhoneNumberNetworkState() : MutableLiveData<NetworkState> {
        return sendPhoneNumberNetworkDataSource.networkState
    }

    fun sendVerifiedNumberNetworkState() : MutableLiveData<NetworkState> {
        return sendVerifiedNumberNetworkDataSource.networkState
    }
}