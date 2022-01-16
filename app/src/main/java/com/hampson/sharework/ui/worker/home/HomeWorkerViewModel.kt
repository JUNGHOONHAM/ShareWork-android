package com.hampson.sharework.ui.worker.home

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception

class HomeWorkerViewModel (private val homeWorkerRepository: HomeWorkerRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val jobInMapLiveData: MediatorLiveData<Response> = MediatorLiveData()
    val jobInMapNetworkState: MediatorLiveData<NetworkState> = MediatorLiveData()

    val searchPositionLiveData: MutableLiveData<LatLng> = MutableLiveData()

    fun getSearchPosition(): LiveData<LatLng> {
        return searchPositionLiveData
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getJobInMap(northeastLat: Double, northeastLng: Double, southwestLat: Double, southwestLng: Double) {
        val repositoryLiveData: LiveData<Response> =
            homeWorkerRepository.getJobInMap(compositeDisposable, northeastLat, northeastLng, southwestLat, southwestLng)

        val repositoryNetworkState: LiveData<NetworkState> = homeWorkerRepository.getJobNetworkState()

        jobInMapLiveData.addSource(repositoryLiveData) { value: Response ->
            jobInMapLiveData.value = value
        }

        jobInMapNetworkState.addSource(repositoryNetworkState) { value: NetworkState ->
            jobInMapNetworkState.value = value
        }
    }

    fun searchMap(searchName: String, geocoder: Geocoder) {
        var addresses: List<Address>? = null

        try {
            addresses = geocoder.getFromLocationName(searchName, 3)
            if (addresses != null && !addresses.equals(" ")) {
                getAddressInfo(addresses)
            }
        } catch (e: Exception) {

        }
    }

    private fun getAddressInfo(addresses: List<Address>) {
        val address = addresses[0]
        val latLng = LatLng(address.latitude, address.longitude)

        searchPositionLiveData.postValue(latLng)
    }
}