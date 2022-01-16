package com.hampson.sharework.ui.membership.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Response
import com.hampson.sharework.data.vo.SmsAuth
import com.hampson.sharework.data.vo.User
import com.hampson.sharework.data.vo.UserRequest
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel (private val repository: SignUpRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val userLiveData: MediatorLiveData<Response> = MediatorLiveData()
    val userNetworkState: MediatorLiveData<NetworkState> = MediatorLiveData()

    fun signUp(user: UserRequest) {
        val repositoryLiveData: LiveData<Response> = repository.signUp(compositeDisposable, user)
        val repositoryNetworkState: LiveData<NetworkState> = repository.signUpNetworkState()

        userLiveData.addSource(repositoryLiveData) { value: Response ->
            userLiveData.value = value
        }

        userNetworkState.addSource(repositoryNetworkState) { value: NetworkState ->
            userNetworkState.value = value
        }
    }
}