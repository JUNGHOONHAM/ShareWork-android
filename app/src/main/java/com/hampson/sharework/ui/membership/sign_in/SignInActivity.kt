package com.hampson.sharework.ui.membership.sign_in

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.hampson.sharework.R
import com.hampson.sharework.common.RegularExpression
import com.hampson.sharework.data.api.DBClient
import com.hampson.sharework.data.api.DBInterface
import com.hampson.sharework.data.repository.NetworkState
import com.hampson.sharework.databinding.ActivitySignInBinding
import com.hampson.sharework.ui.AlertDialog.DialogOneButton
import com.hampson.sharework.ui.ShareWorkBaseActivity
import com.hampson.sharework.ui.membership.sign_up.SignUpActivity
import com.hampson.sharework.ui.worker.MainActivity

class SignInActivity : ShareWorkBaseActivity() {

    private lateinit var binding: ActivitySignInBinding

    private lateinit var viewModel: SignInViewModel
    private lateinit var repository: SignInRepository
    private lateinit var apiService: DBInterface

    private val maxCount = 300

    private lateinit var phoneNumber: String
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)

        setContentView(binding.root)

        apiService = DBClient.getClient(this)
        repository = SignInRepository(apiService)
        viewModel = getViewModel()

        binding.signIn = this
        binding.toolbar.textViewToolbarTitle.text = getString(R.string.sign_in_sign_up)

        binding.linearLayoutSignIn.isEnabled = false

        viewModel.tokenLiveData.observe(this, {
            token = it.payload?.smsAuth?.token
        })

        viewModel.smsAuthLiveData.observe(this, {
            if (!it.status) { // 실패
                DialogOneButton(this)
                    .setTitle(getString(R.string.title_notice))
                    .setMessage(it.message)
                    .setPositiveButton(R.string.title_ok) {

                    }.show()
            } else { // 성공
                if (it.payload?.smsAuth?.user == null) { // 비회원
                    val intent = Intent(this, SignUpActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    slideActivity(intent)
                } else { // 회원
                    val intent = Intent(this, MainActivity::class.java)
                    slideActivity(intent)
                    finish()
                }
            }
        })

        viewModel.smsAuthNetworkState.observe(this, {
            showProgressBar(it)
        })

        binding.editTextCertification.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setBtnLoginCheck()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.linearLayoutCertificationButton.setOnClickListener {

            if (!validationCheckPhoneNumber()) {
                Toast.makeText(this, getString(R.string.message_not_valid_phone_number), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Handler(Looper.getMainLooper()).postDelayed({
                binding.editTextPhoneNumber.clearFocus()
                binding.editTextCertification.isFocusableInTouchMode = true
                binding.editTextCertification.requestFocus()
            }, 30)

            phoneNumber = binding.editTextPhoneNumber.text.toString()

            setBtnCertificationDisAble()
            startCountDown()

            viewModel.sendPhoneNumber(binding.editTextPhoneNumber.text.toString())
        }

        binding.linearLayoutSignIn.setOnClickListener {
            viewModel.sendVerifiedNumber(phoneNumber, token ?: "", binding.editTextCertification.text.toString())
        }

        setHideKeyboard(binding.scrollView)
    }

    // 휴대폰 번호 유효성 체크
    private fun validationCheckPhoneNumber() : Boolean {
        return binding.editTextPhoneNumber.text.toString().matches(RegularExpression.phoneNumberReg)
    }

    // 로그인버튼 check
    private fun setBtnLoginCheck() {
        if (binding.editTextCertification.text.toString() == "") {
            binding.linearLayoutSignIn.setBackgroundColor(Color.parseColor("#afafaf"))
            binding.linearLayoutSignIn.isEnabled = false
            binding.linearLayoutSignIn.isClickable = false
        } else {
            binding.linearLayoutSignIn.setBackgroundColor(Color.parseColor("#64d8d1"))
            binding.linearLayoutSignIn.isEnabled = true
            binding.linearLayoutSignIn.isClickable = true
        }
    }

    // 인증버튼 활성화
    private fun setBtnCertificationEnAble() {
        binding.textViewCertification.text = getString(R.string.certification)
        binding.linearLayoutCertificationButton.isEnabled = true
    }

    // 인증버튼 비활성화
    private fun setBtnCertificationDisAble() {
        binding.linearLayoutCertification.visibility = View.VISIBLE

        binding.textViewCertification.text = getString(R.string.success_send)
        binding.linearLayoutCertificationButton.isEnabled = false

        binding.editTextCertification.clearFocus()
        binding.editTextPhoneNumber.isFocusableInTouchMode = true
        binding.editTextPhoneNumber.requestFocus()
    }

    private fun startCountDown() {
        var CDT: CountDownTimer = object : CountDownTimer(10 * 30000, 1000) {
            var count = maxCount

            override fun onTick(millisUntilFinished: Long) {
                binding.textViewCount.text = count.toString() + "초"
                count--
            }

            override fun onFinish() {
                binding.editTextCertification.setText("")
                binding.linearLayoutCertification.visibility = View.GONE
                Toast.makeText(this@SignInActivity, getString(R.string.message_time_out), Toast.LENGTH_LONG).show()

                setBtnLoginCheck()

                setBtnCertificationEnAble()
            }

        }

        CDT.start()
    }

    private fun getViewModel(): SignInViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T{
                @Suppress("UNCHECKED_CAST")
                return SignInViewModel(repository) as T
            }
        }).get(SignInViewModel::class.java)
    }
}