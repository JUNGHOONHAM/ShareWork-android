package com.hampson.sharework.ui.worker.detail_job

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.hampson.sharework.R
import com.hampson.sharework.databinding.ActivityDetailJobWorkerBinding
import com.hampson.sharework.databinding.ActivityMainWorkerBinding

class DetailJobActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailJobWorkerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailJobWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.test.setOnClickListener {
            var intent = Intent(this, DetailJobActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}