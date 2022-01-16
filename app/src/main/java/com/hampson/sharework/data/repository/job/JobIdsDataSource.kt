package com.hampson.sharework.data.repository.job

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.api.FIRST_PAGE
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.Job
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class JobIdsDataSource (private val apiService : DBInterface, private val compositeDisposable: CompositeDisposable,
                        private var jobIdList: ArrayList<Int>) : PageKeyedDataSource<Int, Job>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getJobIds(jobIdList, params.key, 5, 0.0, 0.0)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.meta?.totalPage ?: 0 >= params.key) {
                            if (it.payload?.jobs?.isNotEmpty() == true) {
                                callback.onResult(it.payload.jobs, params.key + 1)
                            }
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            networkState.postValue(NetworkState.ENDOFLIST)
                        }
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Job>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Job>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getJobIds(jobIdList, page, 5, 0.0, 0.0)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.payload?.jobs?.isNotEmpty() == true) {
                            callback.onResult(it.payload.jobs, null, page + 1)
                        }
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

}