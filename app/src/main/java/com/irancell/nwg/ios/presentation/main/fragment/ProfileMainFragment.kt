package com.irancell.nwg.ios.presentation.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.databinding.FragmentProfileMainBinding
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.presentation.main.viewModel.FragmentProfileMainViewModel
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ProfileMainFragment :
    BaseFragment<FragmentProfileMainBinding, FragmentProfileMainViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentProfileMainBinding {
        return FragmentProfileMainBinding.inflate(layoutInflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).viewBinding.actionBar.search.visibility = View.GONE
        (requireActivity() as MainActivity).viewBinding.actionBar.logo.root.visibility = View.VISIBLE
        viewModel.getProfile().observe(lifecycleOwner) {
            when (it.status) {
                AsyncStatus.LOADING -> {
                    Log.i("sddsd", "initView: LOADING")
                    loading.showLoading()
                }
                AsyncStatus.SUCCESS -> {
                    it.data?.let { profileDomain ->

                        viewBinding.tvNameFname.text = String.format(
                            "%1s\t%2s",
                            profileDomain[0].first_name?.trim(),
                            profileDomain[0].last_name?.trim()
                        )
                        profileDomain[0].role?.let { roleDomain ->
                            if (roleDomain.isNotEmpty())
                                viewBinding.tvRole.text = roleDomain[0].name
                        }
                        viewBinding.tvPhone.text = profileDomain[0].phone_number?.trim()
                        viewBinding.tvCompany.text =  profileDomain[0].company
                        viewBinding.tvNationalId.text = profileDomain[0].national_id
                        viewBinding.tvOrganization.text = profileDomain[0].organization
                        viewBinding.tvEmail.text = profileDomain[0].user?.email
                    }
                    loading.dismissLoading()
                }
                AsyncStatus.ERROR -> {

                    showToast(requireContext(), R.string.unableToGetProfileDetails)
                    loading.dismissLoading()
                }
            }
        }
    }

    override fun onShow() {
    }



    override fun initActionBar(): SearchActionBarBinding {
        return (requireActivity() as MainActivity).viewBinding.actionBar
    }

    override fun initViewModel(): FragmentProfileMainViewModel {
        val fragmentProfileMainViewModel: FragmentProfileMainViewModel by viewModels()
        return fragmentProfileMainViewModel
    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}