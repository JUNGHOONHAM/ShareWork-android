package com.hampson.sharework.ui.worker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.hampson.sharework.R
import com.hampson.sharework.databinding.ActivityMainWorkerBinding
import com.hampson.sharework.ui.ShareWorkBaseActivity

class MainActivity : ShareWorkBaseActivity() {

    private lateinit var binding: ActivityMainWorkerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setHideKeyboard(binding.linearLayout)

        // 네비게이션들을 담는 호스트
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHost) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바텀 네비게이션뷰 와 네비게이션을 묶어준다
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
    }
}