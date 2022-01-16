package com.hampson.sharework.ui.membership.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Response
import com.hampson.sharework.data.vo.SmsAuth
import io.reactivex.disposables.CompositeDisposable

class SignInViewModel (private val signInRepository: SignInRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val tokenLiveData: MediatorLiveData<Response> = MediatorLiveData()
    val smsAuthLiveData: MediatorLiveData<Response> = MediatorLiveData()

    val smsAuthNetworkState: MediatorLiveData<NetworkState> = MediatorLiveData()

    fun sendPhoneNumber(phoneNumber: String) {
        val repositoryLiveData: LiveData<Response> = signInRepository.sendPhoneNumber(compositeDisposable, phoneNumber)

        tokenLiveData.addSource(repositoryLiveData) { value: Response ->
            tokenLiveData.value = value
        }
    }

    fun sendVerifiedNumber(phoneNumber: String, token: String, verifiedNumber: String) {
        val repositoryLiveData: LiveData<Response> =
            signInRepository.sendVerifiedNumber(compositeDisposable, phoneNumber, token, verifiedNumber)

        val repositoryNetworkState: LiveData<NetworkState> = signInRepository.sendVerifiedNumberNetworkState()

        smsAuthLiveData.addSource(repositoryLiveData) { value: Response ->
            smsAuthLiveData.value = value
        }

        smsAuthNetworkState.addSource(repositoryNetworkState) {value: NetworkState ->
            smsAuthNetworkState.value = value
        }
    }
}