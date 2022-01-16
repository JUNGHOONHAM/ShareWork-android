package com.hampson.sharework.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.hampson.sharework.R
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.data.vo.User
import com.hampson.sharework.session.SessionManagement

open class ShareWorkBaseActivity : AppCompatActivity() {

    var progressBar: ProgressDialog? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = ProgressDialog(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    fun slideActivity(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun hideKeyBoard(activity: Activity) {
        var view = activity.currentFocus

        if (view == null) {
            view = View(activity)
        }

        val finalView: View = view

        view.postDelayed(Runnable {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(finalView.applicationWindowToken, 0)
        }, 100)
    }

    fun setHideKeyboard(view: View) {
        view.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                hideKeyBoard(this)
                view.clearFocus()
            }

            return@setOnTouchListener false
        }
    }

    fun showProgressBar(networkState: NetworkState) {
        if (networkState == NetworkState.LOADING) progressBar?.show() else progressBar?.dismiss()
    }

    fun saveSession(user: User?) {
        if (user != null) {
            val sessionManagement = SessionManagement(this)
            sessionManagement.saveSession(user)
        }
    }
}