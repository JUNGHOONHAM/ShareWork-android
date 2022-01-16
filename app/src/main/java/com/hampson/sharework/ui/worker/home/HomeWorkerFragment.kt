package com.hampson.sharework.ui.worker.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.compose.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.hampson.sharework.R
import com.hampson.sharework.data.api.DBClient
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.vo.Job
import com.hampson.sharework.databinding.FragmentHomeWorkerBinding
import com.hampson.sharework.ui.ShareWorkBaseActivity
import com.hampson.sharework.ui.ShareWorkBaseFragment
import com.hampson.sharework.ui.worker.MainActivity
import com.hampson.sharework.ui.worker.detail_job.DetailJobActivity
import com.hampson.sharework.ui.worker.home.job_list.BottomSheetJobList
import com.leinardi.android.speeddial.SpeedDialView

class HomeWorkerFragment : ShareWorkBaseFragment(), OnMapReadyCallback, ClusterManager.OnClusterClickListener<HomeWorkerFragment.MyClusterItem> {

    private lateinit var binding: FragmentHomeWorkerBinding

    private lateinit var mView : MapView

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var map: GoogleMap

    private lateinit var clusterManager: ClusterManager<MyClusterItem>
    private var clusterRenderer: ClusterRenderer? = null

    private lateinit var viewModel: HomeWorkerViewModel
    private lateinit var repository: HomeWorkerRepository
    private lateinit var apiService: DBInterface

    private var myLocation: LatLng? = null

    private lateinit var speedDialView: SpeedDialView

    private var userId: Int = -1

    private lateinit var imm: InputMethodManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeWorkerBinding.inflate(inflater, container, false)

        speedDialView = binding.speedDial

        mView = binding.map
        mView.onCreate(savedInstanceState)
        mView.getMapAsync(this)
        //mView.getMapAsync {
        //    clusterRenderer = ClusterRenderer(requireActivity(), it, clusterManager)
        //}

        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        apiService = DBClient.getClient(context ?: requireContext())
        repository = HomeWorkerRepository(apiService)
        viewModel = getViewModel()

        binding.root.setOnClickListener {
            layoutTouchKeybordReset(activity as MainActivity)
        }

        viewModel.jobInMapLiveData.observe(requireActivity(), {
            if (it.status) {
                if (it.payload != null) {
                    addItems(it.payload.jobs)
                }
            }
        })

        viewModel.jobInMapNetworkState.observe(requireActivity(), {
            (context as ShareWorkBaseActivity).showProgressBar(it)
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val defaultLocation = LatLng(37.715133, 126.734086)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 17f))

        locationInit()
        addLocationListener()
        setUpClusterer()
    }

    private fun addItems(jobList: List<Job>) {
        clusterManager.clearItems()

        var offsetItem: MyClusterItem
        for (i in jobList.indices) {
            offsetItem = MyClusterItem(jobList[i].lat, jobList[i].lng, jobList[i].jobId)
            clusterManager.addItem(offsetItem)
        }

        clusterManager.renderer =
            ClusterRenderer(
                requireActivity(),
                map,
                clusterManager
            )
    }

    class ClusterRenderer(
        context: Context?,
        map: GoogleMap?,
        clusterManager: ClusterManager<MyClusterItem>?
    ): DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {

        init {
            clusterManager?.renderer = this
            minClusterSize = 1
        }
    }

    private fun setUpClusterer() {
        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, map)
        clusterManager.setOnClusterClickListener(this)
        clusterManager.renderer =
            ClusterRenderer(
                requireActivity(),
                map,
                clusterManager
            )

        // Point the map's listeners at the listeners implemented by the cluster
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        map.setOnCameraIdleListener {
            var northeast = map.projection.visibleRegion.latLngBounds.northeast
            var southwest = map.projection.visibleRegion.latLngBounds.southwest

            viewModel.getJobInMap(northeast.latitude, northeast.longitude, southwest.latitude, southwest.longitude)
        }
    }

    inner class MyClusterItem(
        lat: Double,
        lng: Double,
        jobId: Int
    ) : ClusterItem {

        private val position: LatLng
        private val jobId: Int

        fun getJob_id(): Int {
            return jobId
        }

        override fun getSnippet(): String? {
            TODO("Not yet implemented")
        }

        override fun getTitle(): String? {
            TODO("Not yet implemented")
        }

        override fun getPosition(): LatLng {
            return position
        }

        init {
            position = LatLng(lat, lng)
            this.jobId = jobId
        }
    }

    private fun locationInit() {
        fusedLocationProviderClient = FusedLocationProviderClient(requireActivity())
        locationCallback = MyLocationCallBack()

        locationRequest = LocationRequest()   // LocationRequest객체로 위치 정보 요청 세부 설정을 함
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // GPS 우선
        //locationRequest.interval = 10000   // 10초. 상황에 따라 다른 앱에서 더 빨리 위치 정보를 요청하면
        // 자동으로 더 짧아질 수도 있음
        //locationRequest.fastestInterval = 5000  // 이보다 더 빈번히 업데이트 하지 않음 (고정된 최소 인터벌)
    }

    private fun addLocationListener() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return
        }

        map.isMyLocationEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = false

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
            locationCallback,
            null)  // 혹시 안드로이드 스튜디오에서 비정상적으로 권한 요청 오류를 표시할 경우, 'Alt+Enter'로
        // 표시되는 제안 중, Suppress: Add @SuppressLint("MissingPermission") annotation
        // 을 클릭할 것
        // (에디터가 원래 권한 요청이 필요한 코드 주변에서만 권한 요청 코딩을 허용했었기 때문임.
        //  현재 우리 코딩처럼 이렇게 별도의 메소드에 권한 요청 코드를 작성하지 못하게 했었음)
    }

    inner class MyLocationCallBack: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            val location = locationResult?.lastLocation   // GPS가 꺼져 있을 경우 Location 객체가
            // null이 될 수도 있음

            myLocation = location?.latitude?.let { LatLng(it, location.longitude) }!!   // 위도, 경도
            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation))  // 카메라 이동

            location.run {
                myLocation = LatLng(latitude, longitude)   // 위도, 경도
            }
        }
    }

    private fun permissionCheck(cancel: () -> Unit, ok: () -> Unit) { // 전달인자도, 리턴값도 없는
        // 두 개의 함수를 받음
        if (ContextCompat.checkSelfPermission(requireActivity(), // 권한이 없는 경우
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )) { // 권한 거부 이력이 있는 경우
                cancel()
            } else {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION)
            }
        } else { // 권한이 있는 경우
            ok()
        }
    }

    private fun showPermissionInfoDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("알림")
            .setMessage("위치 권한을 허용하셔야 서비스 이용이 가능합니다.")
            .setPositiveButton("OK"){
                    dialog, which -> ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("CANCEL",null)
            .create()

        alertDialog.show()
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    addLocationListener()
                } else {
                    Toast.makeText(context, "위치 권한을 허용하셔야 서비스 이용이 가능합니다.", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun removeLocationListener() {
        if (this::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }


    override fun onStart() {
        super.onStart()
        mView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mView.onResume()

        permissionCheck(cancel = {
            showPermissionInfoDialog()
        }, ok = {
            if (this::map.isInitialized) {
                locationInit()
                addLocationListener()
            }

        })
    }

    override fun onPause() {
        super.onPause()
        mView.onPause()
        removeLocationListener()    // 앱이 동작하지 않을 때에는 위치 정보 요청 제거
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView.onLowMemory()
    }

    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }

    override fun onClusterClick(cluster: Cluster<MyClusterItem>?): Boolean {
        val item = cluster?.items?.toTypedArray()
        var jobIdList = ArrayList<Int>()

        if (item != null) {
            for (i in item.indices) {
                jobIdList.add(item[i].getJob_id())
            }
        }

        val fragment = BottomSheetJobList()
        val bundle = Bundle()
        bundle.putIntegerArrayList("jobIdList", jobIdList)
        fragment.arguments = bundle

        (requireActivity()).supportFragmentManager.beginTransaction().add(
            fragment, "com.hampson.sharework.test")
            .commit()

        return true
    }

    private fun getViewModel(): HomeWorkerViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return HomeWorkerViewModel(repository) as T
            }
        }).get(HomeWorkerViewModel::class.java)
    }
}