package com.irancell.nwg.ios.presentation.main.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.irancell.nwg.ios.IMAGE_QUALITY
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.model.Setting
import com.irancell.nwg.ios.databinding.FragmentSettingMainBinding
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.util.ENGLISH
import com.irancell.nwg.ios.util.HIGH
import com.irancell.nwg.ios.util.LOW
import com.irancell.nwg.ios.util.MEDIUM
import com.irancell.nwg.ios.util.constants.PERSIAN

import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.presentation.main.SENDER
import com.irancell.nwg.ios.presentation.main.viewModel.FragmentSettingMainViewModel
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SettingMainFragment : BaseFragment<FragmentSettingMainBinding, FragmentSettingMainViewModel>() {


    override fun onBackPressed() {
    }

    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentSettingMainBinding {
        return FragmentSettingMainBinding.inflate(layoutInflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).viewBinding.actionBar.search.visibility = View.GONE
        (requireActivity() as MainActivity).viewBinding.actionBar.logo.root.visibility = View.VISIBLE
        (requireActivity() as MainActivity).viewBinding.actionBar.txtLoadedPage.text = requireContext().getString(R.string.action_settings)

        Log.i("initView", "FragmentSettingMain: ")
        viewModel.checkSettings().observe(lifecycleOwner){
            settings->
                settings?.let {
                    Log.i("initView", "FragmentSettingMain: observe")
                    setSelectLanguage(it)
                    setSelectQuality(it)
                }

        }
    }

    private fun setSelectQuality(it: List<Setting>) {
        val selectedQuality =  it.first { quality->  quality.key == IMAGE_QUALITY }

        when(selectedQuality.value){
            HIGH-> viewBinding.rbHigh.isChecked = true
            MEDIUM-> viewBinding.rbMedium.isChecked = true
            LOW-> viewBinding.rbLow.isChecked = true
        }
        viewBinding.rgQuality.setOnCheckedChangeListener { group , checkId ->
            when (checkId) {
                viewBinding.rbLow.id -> {
                    viewModel.updateImageQuality(LOW)
                }
                viewBinding.rbMedium.id -> {
                    viewModel.updateImageQuality(MEDIUM)
                }
                viewBinding.rbHigh.id -> {
                    viewModel.updateImageQuality(HIGH)
                }
            }

        }
    }

    private fun setSelectLanguage(it: List<Setting>) {
        val selectedLanguage =  it.first { language->  language.key == SELECTED_LANGUAGE }
        when(selectedLanguage.value){
            PERSIAN-> viewBinding.rbPersian.isChecked = true
            ENGLISH-> viewBinding.rbEnglish.isChecked = true
        }
        viewBinding.rgLanguages.setOnCheckedChangeListener { group, checkId ->
            loading.showLoading()
            when (checkId) {
                viewBinding.rbEnglish.id -> {
                    viewModel.updateLanguage(ENGLISH)
                }
                viewBinding.rbPersian.id -> {
                    viewModel.updateLanguage(PERSIAN)
                }
            }

            startActivity(Intent(requireActivity(), MainActivity::class.java).putExtra(SENDER,SettingMainFragment::class.java.simpleName) )
            requireActivity().finish()
            loading.dismissLoading()

        }
    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }
    override fun onShow() {

    }



    override fun initViewModel(): FragmentSettingMainViewModel {
        val fragmentSettingMainViewModel : FragmentSettingMainViewModel by viewModels()
        return fragmentSettingMainViewModel
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}