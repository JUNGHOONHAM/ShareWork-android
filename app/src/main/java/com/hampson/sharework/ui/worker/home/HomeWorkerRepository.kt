package com.hampson.sharework.ui.worker.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.repository.job.JobInMapNetworkDataSource
import com.hampson.sharework.data.vo.Response
import io.reactivex.disposables.CompositeDisposable

class HomeWorkerRepository (private val apiService : DBInterface) {
    lateinit var jobInMapNetworkDataSource: JobInMapNetworkDataSource

    fun getJobInMap(compositeDisposable: CompositeDisposable, northeastLat: Double, northeastLng: Double,
                    southwestLat: Double, southwestLng: Double) : MutableLiveData<Response> {

        jobInMapNetworkDataSource = JobInMapNetworkDataSource(apiService, compositeDisposable)
        jobInMapNetworkDataSource.getJobInMap(northeastLat, northeastLng, southwestLat, southwestLng)

        return jobInMapNetworkDataSource.downlodedResponse
    }

    fun getJobNetworkState(): LiveData<NetworkState> {
        return jobInMapNetworkDataSource.networkState
    }
}