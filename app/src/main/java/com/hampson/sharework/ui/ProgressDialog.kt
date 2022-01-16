package com.hampson.sharework.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.hampson.sharework.R

class ProgressDialog : Dialog {
    constructor(context: Context) : super(context) {
        val params: WindowManager.LayoutParams = window?.attributes ?: WindowManager.LayoutParams()

        params.gravity = Gravity.CENTER_HORIZONTAL
        window?.attributes = params
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setTitle(null)
        setCancelable(false)
        setOnCancelListener(null)

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
        setContentView(view)
    }
}