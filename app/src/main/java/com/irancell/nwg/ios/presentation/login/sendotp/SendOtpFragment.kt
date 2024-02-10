package com.irancell.nwg.ios.presentation.login.sendotp

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.databinding.FragmentSendOtpBinding
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.util.NETWORK_UNREACHABLE
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.EMAIL
import com.irancell.nwg.ios.util.constants.PASSWORD
import com.irancell.nwg.ios.util.constants.SESSION_ID
import com.irancell.nwg.ios.util.setAnimationClick
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SendOtpFragment : BaseFragment<FragmentSendOtpBinding, SendOtpViewModel>() {

    @Inject
    lateinit var sharedPref: SharedPref

    var hidePass: Boolean = true
    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentSendOtpBinding {
        return FragmentSendOtpBinding.inflate(layoutInflater, container, false)
    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(savedInstanceState: Bundle?) {
        viewBinding.imgShowpass.setOnClickListener {
            if (hidePass) {
                viewBinding.imgShowpass.setImageDrawable(
                    getDrawable(
                        requireContext(),
                        R.drawable.ic_password_show
                    )
                )
                hidePass = false
                viewBinding.etPassword.transformationMethod = null
            } else {
                viewBinding.imgShowpass.setImageDrawable(
                    getDrawable(
                        requireContext(),
                        R.drawable.ic_password_hide
                    )
                )
                hidePass = true
                viewBinding.etPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }


        viewBinding.cardSendOtp.setAnimationClick(lifecycleScope,R.anim.button_click) {
            viewModel.requestSession(
                viewBinding.etUsername.text.toString().trim(),
                viewBinding.etPassword.text.toString().trim()
            )
                .observe(lifecycleOwner) {
                    when (it.status) {
                        AsyncStatus.LOADING -> {
                            Log.i("requestSession", "initView: LOADING")
                            loading.showLoading()
                        }
                        AsyncStatus.SUCCESS -> {
                            sharedPref.putString(EMAIL , viewBinding.etUsername.text.toString().trim())
                            sharedPref.putString(PASSWORD , viewBinding.etPassword.text.toString().trim())
                            it.data?.let {sessionResponse->sharedPref.putString(SESSION_ID,sessionResponse.session_id)}
                            navigate(R.id.verifyOtpFragment)
                            loading.dismissLoading()

                        }
                        AsyncStatus.ERROR -> {

                            if (it.code == NETWORK_UNREACHABLE) {
                                showToast(requireContext(), R.string.networkError)
                            }
                            loading.dismissLoading()
                        }
                    }
                }
        }

    }

    override fun onShow() {
    }


    override fun initViewModel(): SendOtpViewModel {
        val sendOtpViewModel: SendOtpViewModel by viewModels()
        return sendOtpViewModel
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}

