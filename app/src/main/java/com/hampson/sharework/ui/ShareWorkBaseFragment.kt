package com.hampson.sharework.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hampson.sharework.R
import com.hampson.sharework.data.vo.User
import com.hampson.sharework.session.SessionManagement

open class ShareWorkBaseFragment : Fragment() {

    fun slideActivity(intent: Intent) {
        startActivity(intent)
        (activity as ShareWorkBaseActivity).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    fun layoutTouchKeybordReset(activity: Activity) {
        //(activity as ShareWorkBaseActivity).layoutTouchKeybordReset(activity)
    }

    fun saveSession(user: User) {
        val sessionManagement = SessionManagement(requireContext())
        sessionManagement.saveSession(user)
    }
}