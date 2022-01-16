package com.hampson.sharework.data.repository.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class SignUpNetworkDataSource (private val apiService: DBInterface, private val compositeDisposable: CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: MutableLiveData<NetworkState>
        get() = _networkState

    private val _downloadedResponse = MutableLiveData<Response>()
    val downlodedResponse: MutableLiveData<Response>
        get() = _downloadedResponse

    fun signUp(user: UserRequest) {
        _networkState.postValue(NetworkState.LOADING)   

        try {
            compositeDisposable.add(
                apiService.signUp(user)
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