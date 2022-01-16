package com.hampson.sharework.ui.worker.home.job_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.api.POST_PER_PAGE
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.repository.job.JobIdsDataSource
import com.hampson.sharework.data.repository.job.JobIdsDataSourceFactory
import com.hampson.sharework.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class JobPagedListRepository (private val apiService : DBInterface) {

    private lateinit var jobPagedList: LiveData<PagedList<Job>>
    private lateinit var jobDataSourceFactory: JobIdsDataSourceFactory

    fun fetchLiveJobPagedList (compositeDisposable: CompositeDisposable, jobIdList: ArrayList<Int>) : LiveData<PagedList<Job>> {
        jobDataSourceFactory = JobIdsDataSourceFactory(apiService, compositeDisposable, jobIdList)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        jobPagedList = LivePagedListBuilder(jobDataSourceFactory, config).build()

        return jobPagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<JobIdsDataSource, NetworkState>(
            jobDataSourceFactory.jobLiveDataSource, JobIdsDataSource::networkState
        )
    }
}