package com.hampson.sharework.ui.AlertDialog

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import com.hampson.sharework.R

class DialogOneButton(private val context: Context) {
    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(context).setView(view)
    }

    private val view: View by lazy {
        View.inflate(context, R.layout.dialog_alert_one_button, null)
    }

    private var dialog: AlertDialog? = null

    // 터치 리스너 구현
    private val onTouchListener = View.OnTouchListener { _, motionEvent ->
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            Handler(Looper.myLooper()!!).postDelayed({
                dismiss()
            }, 5)
        }
        false
    }

    fun setTitle(@StringRes titleId: Int): DialogOneButton {
        view.findViewById<TextView>(R.id.text_view_title).text = context.getText(titleId)
        return this
    }

    fun setTitle(title: CharSequence): DialogOneButton {
        view.findViewById<TextView>(R.id.text_view_title).text = title
        return this
    }

    fun setMessage(@StringRes messageId: Int): DialogOneButton {
        view.findViewById<TextView>(R.id.text_view_message).text = context.getText(messageId)
        return this
    }

    fun setMessage(message: CharSequence): DialogOneButton {
        view.findViewById<TextView>(R.id.text_view_message).text = message
        return this
    }

    fun setPositiveButton(@StringRes textId: Int, listener: (view: View) -> (Unit)): DialogOneButton {
        view.findViewById<LinearLayout>(R.id.linear_layout_ok).apply {
            view.findViewById<TextView>(R.id.text_view_ok).text = context.getText(textId)
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun setPositiveButton(text: CharSequence, listener: (view: View) -> (Unit)): DialogOneButton {
        view.findViewById<LinearLayout>(R.id.linear_layout_ok).apply {
            view.findViewById<TextView>(R.id.text_view_ok).text = text
            setOnClickListener(listener)
            setOnTouchListener(onTouchListener)
        }
        return this
    }

    fun create() {
        dialog = builder.create()
    }

    fun show() {
        create()
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}