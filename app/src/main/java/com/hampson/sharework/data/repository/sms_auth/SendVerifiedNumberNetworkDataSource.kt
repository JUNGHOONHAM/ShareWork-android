package com.hampson.sharework.data.repository.sms_auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Response
import com.hampson.sharework.data.vo.SmsAuth
import com.hampson.sharework.data.vo.SmsAuthRequest
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class SendVerifiedNumberNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: MutableLiveData<NetworkState>
        get() = _networkState

    private val _downloadedResponse = MutableLiveData<Response>()
    val downlodedResponse: MutableLiveData<Response>
        get() = _downloadedResponse

    fun sendVerifiedNumber(phoneNumber: String, token: String, verifiedNumber: String) {
        _networkState.postValue(NetworkState.LOADING)   

        try {
            compositeDisposable.add(
                apiService.sendVerifiedNumber(SmsAuthRequest(phoneNumber, token, verifiedNumber))
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedResponse.postValue(it)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }
}