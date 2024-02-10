package com.irancell.nwg.ios.presentation.login.verifyotp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.databinding.FragmentVerifyOtpBinding
import com.irancell.nwg.ios.eventbus.ReceiveMessage
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.util.NETWORK_UNREACHABLE
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.constants.SESSION_ID
import com.irancell.nwg.ios.util.constants.SESSION_TOKEN
import com.irancell.nwg.ios.util.setAnimationClick
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
var counter = 30000L

/**
 * A simple [Fragment] subclass.
 * Use the [VerifyOtpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class VerifyOtpFragment : BaseFragment<FragmentVerifyOtpBinding, VerifyOtpViewModel>() {
    @Inject
    lateinit var sharedPref: SharedPref


    var countDownTimer: CountDownTimer =
        object : CountDownTimer(counter, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val countdownText = String.format(
                    "%1s : %2s",
                    (millisUntilFinished / 1000) / 60,
                    (millisUntilFinished / 1000) % 60
                )
                viewBinding.tvCountDown.text = String.format(countdownText)
            }

            override fun onFinish() {
                enableDisableResendCode(true)
            }
        }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentVerifyOtpBinding {
        return FragmentVerifyOtpBinding.inflate(layoutInflater, container, false)
    }

    fun enableDisableResendCode(enabled: Boolean) {
        if (enabled) {
            counter = 30000L
            viewBinding.linResendCode.backgroundTintList = ContextCompat.getColorStateList(requireContext(),R.color.colorPrimary)
            viewBinding.tvResendCode.setTextColor(resources.getColor(R.color.black))
            viewBinding.tvResendCode.isEnabled = true
        } else {
            viewBinding.linResendCode.backgroundTintList =  ContextCompat.getColorStateList(requireContext(),R.color.colorPrimaryTransparent)
            viewBinding.tvResendCode.setTextColor(ContextCompat.getColor(requireContext(),R.color.transparentBlack))
            viewBinding.tvResendCode.isEnabled = false
        }
    }

    fun requestToken(smsContent: String) {
        viewModel.requestToken(smsContent)
            .observe(lifecycleOwner) {
                when (it.status) {
                    AsyncStatus.LOADING -> {
                        loading.showLoading()
                    }
                    AsyncStatus.SUCCESS -> {
                        it.data?.let { tokenResponse ->
                            sharedPref.putString(SESSION_TOKEN, tokenResponse.auth_token)
                            sharedPref.remove(SESSION_ID)
                            getProfile()
                        }
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


    override fun initView(savedInstanceState: Bundle?) {
//        viewBinding.etVerificationCode.isEnabled =false
        enableDisableResendCode(false)
        countDownTimer.start()
        viewBinding.etVerificationCode.doAfterTextChanged {
            if (it?.length == 6)
                requestToken(it.toString())
        }


        Log.i("resendVerifyCode", "initView: " + sharedPref.getString(SESSION_ID))
        viewBinding.tvResendCode.setAnimationClick(lifecycleScope,R.anim.button_click) {
            countDownTimer.start()
            enableDisableResendCode(false)
            viewModel.resendVerifyCode()
                .observe(lifecycleOwner) {
                    when (it.status) {
                        AsyncStatus.LOADING -> {
                            Log.i("resendVerifyCode", "initView: LOADING")
                            loading.showLoading()
                        }
                        AsyncStatus.SUCCESS -> {
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

//        viewBinding.tvResendCode.setAnimationClick(R.anim.button_click) {
//            viewBinding.etVerificationCode.setText("")
//            enableDisableResendCode(false)
//            countDownTimer.start()
//            Log.i("SessionID", "onReceiveMessage: "+(requireActivity() as LoginActivity).sessionId)
//
//            viewModel.resendVerifyCode((requireActivity() as LoginActivity).sessionId)
//                .observe(lifecycleOwner) {
//                    when (it.status) {
//                        ApiStatus.LOADING -> {
//                            loading?.showLoading()
//                        }
//                        ApiStatus.SUCCESS -> {
//                            Toast.makeText(
//                                requireContext(),
//                                getString(R.string.resend_code_number),
//                                Toast.LENGTH_LONG
//                            ).show()
//                            loading?.dismiss()
//
//                        }
//                        ApiStatus.ERROR -> {
//                            Log.i("errrrrr", "initView: "+it.message)
//                            Toast.makeText(
//                                requireContext(),
//                                it.message,
//                                Toast.LENGTH_LONG
//                            ).show()
//                            loading?.dismiss()
//                        }
//                    }
//                }
//        }

    }

    override fun onShow() {
    }

    override fun onBackPressed() {
        super.onBackPressed()
        countDownTimer.cancel()
        navigateUp()
    }

    override fun initViewModel(): VerifyOtpViewModel {
        val verifyOtpViewModel: VerifyOtpViewModel by viewModels()
        return verifyOtpViewModel
    }


    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        bundle: Bundle
    ) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceiveMessage(event: ReceiveMessage) {
//        //get the phone number value here and do something with it
        val smsContent: String = event.smsContent
        viewBinding.etVerificationCode.setText(event.smsContent)
        Log.i("onReceiveMessage", "onPhoneNumberReceived: " + smsContent)


    }

    private fun getProfile() {
        viewModel.getProfile().observe(requireActivity()) {
            when (it.status) {
                AsyncStatus.LOADING -> {
                }
                AsyncStatus.SUCCESS -> {
                    updateProjects()
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

    private fun updateProjects() {
        viewModel.updateProjects().observe(lifecycleOwner)
        {
            when (it.status) {
                AsyncStatus.LOADING -> {
                    loading.showLoading()
                    Log.i("updateProjects", "updateProjects:LOADING ")
                }
                AsyncStatus.SUCCESS -> {
                    getProjectIds()
                    Log.i("updateProjects", "updateProjects:SUCCESS ")
                }
                AsyncStatus.ERROR -> {
                    loading.dismissLoading()
                    Log.i("updateProjects", "updateProjects:ERROR " + it.message)

                }
            }
        }
    }

    private fun getProjectIds() {
        viewModel.getProjectIds().observe(lifecycleOwner) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    Log.i("getProjectIds", "updateSites: SUCCESS  ")
                    updateSites(it.data!!)
                }
                AsyncStatus.ERROR -> {
                    loading.dismissLoading()

                    Log.i("getProjectIds", "updateSites: ERROR  ")


                }
                AsyncStatus.LOADING -> {

                    Log.i("getProjectIds", "updateSites: LOADING  ")
                }
            }
        }


    }

    private fun updateSites(projectIds: List<Int>) {
        viewModel.updateSites(projectIds).observe(lifecycleOwner) {
            when (it.status) {

                AsyncStatus.SUCCESS -> {
                    Log.i("updateSites", "updateSites: SUCCESS ")
                    updateAuditFormat()
                }
                AsyncStatus.ERROR -> {
                    loading.dismissLoading()

                    Log.i("updateSites", "updateSites: error")

                }
                AsyncStatus.LOADING -> {
                    Log.i("updateSites", "updateSites: loading  ")


                }
            }
        }


    }

    private fun updateAuditFormat() {
        viewModel.updateAuditFormat().observe(lifecycleOwner) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    Log.i("updateAuditFormat", "updateAuditFormat: SUCCESS")
                    updateGenerateAudits()


                }
                AsyncStatus.ERROR -> {
                    Log.i("updateAuditFormat", "updateAuditFormat: ERROR" + it.message)
                    loading.dismissLoading()

                }
                AsyncStatus.LOADING -> {


                }
            }

        }

    }

    private fun updateGenerateAudits() {
        viewModel.updateGenerateAudits().observe(lifecycleOwner) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    Log.i("updateGenerateAudits", "updateGenerateAudits: SUCCESS" + it.data)
                    updateProblematicForms()
                }
                AsyncStatus.ERROR -> {
                    Log.i("erroor", "getProfile: "+it.message)
                    loading.dismissLoading()
                }
                AsyncStatus.LOADING -> {
                }
            }
        }

    }

    private fun updateProblematicForms() {
        viewModel.updateProblematicForms().observe(lifecycleOwner){
            when(it.status){
                AsyncStatus.SUCCESS -> {
                    Log.i("updateProblematicForms", "updateProblematicForms: SUCCESS"+it.data)
                    countDownTimer.cancel()
                    requireActivity().finish()
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    loading.dismissLoading()
                }
                AsyncStatus.ERROR -> {

                    loading.dismissLoading()
                }
                AsyncStatus.LOADING -> {

                }
            }
        }

    }
}