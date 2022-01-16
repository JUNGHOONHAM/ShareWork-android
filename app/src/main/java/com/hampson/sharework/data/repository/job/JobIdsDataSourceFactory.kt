package com.hampson.sharework.data.repository.job

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class JobIdsDataSourceFactory (private val apiService: DBInterface, private val compositeDisposable: CompositeDisposable,
                               private var jobIdList: ArrayList<Int>) : DataSource.Factory<Int, Job>() {

    val jobLiveDataSource = MutableLiveData<JobIdsDataSource>()

    override fun create(): DataSource<Int, Job> {
        val jobDataSource = JobIdsDataSource(apiService, compositeDisposable, jobIdList)

        jobLiveDataSource.postValue(jobDataSource)
        return jobDataSource
    }

}