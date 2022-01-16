package com.hampson.sharework.ui.membership.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.repository.sms_auth.SendPhoneNumberNetworkDataSource
import com.hampson.sharework.data.repository.sms_auth.SendVerifiedNumberNetworkDataSource
import com.hampson.sharework.data.repository.user.SignUpNetworkDataSource
import com.hampson.sharework.data.vo.Response
import com.hampson.sharework.data.vo.SmsAuth
import com.hampson.sharework.data.vo.User
import com.hampson.sharework.data.vo.UserRequest
import io.reactivex.disposables.CompositeDisposable

class SignUpRepository (private val apiService : DBInterface)  {
    private lateinit var signUpNetworkDataSource: SignUpNetworkDataSource

    fun signUp (compositeDisposable: CompositeDisposable, user: UserRequest) : MutableLiveData<Response> {
        signUpNetworkDataSource = SignUpNetworkDataSource(apiService, compositeDisposable)
        signUpNetworkDataSource.signUp(user)

        return signUpNetworkDataSource.downlodedResponse
    }

    fun signUpNetworkState() : MutableLiveData<NetworkState> {
        return signUpNetworkDataSource.networkState
    }
}