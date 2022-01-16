package com.hampson.sharework.ui.worker.home.job_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class BottomSheetJobViewModel (private val jobRepository: JobPagedListRepository, private var jobIdList: ArrayList<Int>) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val jobPagedList : LiveData<PagedList<Job>> by lazy {
        jobRepository.fetchLiveJobPagedList(compositeDisposable, jobIdList)
    }

    val networkState : LiveData<NetworkState> by lazy {
        jobRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return jobPagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}