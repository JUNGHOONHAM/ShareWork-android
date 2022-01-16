package com.hampson.sharework.ui.membership.sign_up

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.se.omapi.Session
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hampson.sharework.R
import com.hampson.sharework.common.RegularExpression
import com.hampson.sharework.data.api.DBClient
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.vo.User
import com.hampson.sharework.data.vo.UserRequest
import com.hampson.sharework.databinding.ActivityDetailJobWorkerBinding
import com.hampson.sharework.databinding.ActivitySignInBinding
import com.hampson.sharework.databinding.ActivitySignUpBinding
import com.hampson.sharework.session.SessionManagement
import com.hampson.sharework.ui.AlertDialog.DialogOneButton
import com.hampson.sharework.ui.ShareWorkBaseActivity
import com.hampson.sharework.ui.membership.sign_in.SignInRepository
import com.hampson.sharework.ui.worker.MainActivity

class SignUpActivity : ShareWorkBaseActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var viewModel: SignUpViewModel
    private lateinit var repository: SignUpRepository
    private lateinit var apiService: DBInterface

    private lateinit var phoneNumber: String

    private lateinit var userName: String
    private lateinit var residentNumberFront: String
    private lateinit var residentNumberRear: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = DBClient.getClient(this)
        repository = SignUpRepository(apiService)
        viewModel = getViewModel()

        binding.signIn = this
        binding.toolbar.textViewToolbarTitle.text = getString(R.string.sign_up)

        if (intent.hasExtra("phoneNumber")) {
            phoneNumber = intent.getStringExtra("phoneNumber").toString()
        }

        setHideKeyboard(binding.scrollView)

        viewModel = getViewModel()

        viewModel.userLiveData.observe(this, {
            if (!it.status) {
                DialogOneButton(this)
                    .setTitle(getString(R.string.title_notice))
                    .setMessage(it.message)
                    .setPositiveButton(R.string.title_ok) {

                    }.show()
            } else {
                if (it.payload != null) {
                    saveSession(it.payload.smsAuth?.user)

                    val intent = Intent(this, MainActivity::class.java)
                    slideActivity(intent)
                } else { // 임시
                    val intent = Intent(this, MainActivity::class.java)
                    slideActivity(intent)
                }
            }
        })

        viewModel.userNetworkState.observe(this, {
            showProgressBar(it)
        })

        binding.linearLayoutSignUp.setOnClickListener {
            userName = binding.editTextName.text.toString()
            residentNumberFront = binding.editTextResident.text.toString()
            residentNumberRear = binding.editTextGender.text.toString()
            email = binding.editTextEmail.text.toString()

            if (userValidationCheck()) {
                val user = UserRequest(email, userName, phoneNumber, residentNumberFront, residentNumberRear)
                viewModel.signUp(user)
            }
        }
    }

    private fun userValidationCheck(): Boolean {
        if (userName.trim() == "") {
            Toast.makeText(this, getString(R.string.message_write_name), Toast.LENGTH_LONG).show()
            return false
        }

        if (residentNumberFront.trim() == "" || residentNumberRear.trim() == "") {
            Toast.makeText(this, getString(R.string.message_write_resident), Toast.LENGTH_LONG).show()
            return false
        }

        if (email.trim() == "") {
            Toast.makeText(this, getString(R.string.message_write_email), Toast.LENGTH_LONG).show()
            return false
        }

        if (!birthValidationCheck()) {
            Toast.makeText(this, getString(R.string.message_wrong_resident_number), Toast.LENGTH_LONG).show()
            return false
        }

        if (!emailValidationCheck()) {
            Toast.makeText(this, getString(R.string.message_wrong_email), Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun birthValidationCheck(): Boolean {
        return if (!residentNumberFront.matches(RegularExpression.residentNumberReg)) {
            false
        } else {
            residentNumberRear.matches(RegularExpression.genderNumberReg)
        }
    }

    private fun emailValidationCheck(): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getViewModel(): SignUpViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return SignUpViewModel(repository) as T
            }
        }).get(SignUpViewModel::class.java)
    }
}