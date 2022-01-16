package com.hampson.sharework.data.repository.job

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class JobInMapNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: MutableLiveData<NetworkState>
        get() = _networkState

    private val _downloadedResponse = MutableLiveData<Response>()
    val downlodedResponse: MutableLiveData<Response>
        get() = _downloadedResponse

    fun getJobInMap(northeastLat: Double, northeastLng: Double, southwestLat: Double, southwestLng: Double) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getJobs(northeastLat, northeastLng, southwestLat, southwestLng)
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