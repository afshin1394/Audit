package com.irancell.nwg.ios.presentation.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.databinding.FragmentHomeMainBinding
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.charts.PieChartModel
import com.irancell.nwg.ios.util.charts.createPieChart
import com.irancell.nwg.ios.util.charts.getTextState
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.presentation.main.viewModel.FragmentHomeViewModel
import com.irancell.nwg.ios.util.constants.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeMainFragment : BaseFragment<FragmentHomeMainBinding,FragmentHomeViewModel>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    @Inject
    lateinit var sharedPref: SharedPref

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
    ): FragmentHomeMainBinding {
        return FragmentHomeMainBinding.inflate(layoutInflater,container,false)
    }
    override fun initActionBar(): SearchActionBarBinding {
        return (requireActivity() as MainActivity).viewBinding.actionBar
    }

    override fun initView(savedInstanceState: Bundle?) {

        (requireActivity() as MainActivity).viewBinding.actionBar.search.visibility = View.GONE
        (requireActivity() as MainActivity).viewBinding.actionBar.logo.root.visibility = View.VISIBLE
        viewModel.getSiteStatusDetails().observe(lifecycleOwner){
            val pieChartModels = arrayListOf<PieChartModel>()
            it?.let { siteStates->
                run {
                    for (i in siteStates.indices) {
                        when(siteStates[i].audit_status){
                            Scheduled->{
                                pieChartModels.add(PieChartModel(
                                    siteStates[i].count.toFloat(),
                                    getTextState(requireContext(),siteStates[i].audit_status),
                                    ContextCompat.getColor(requireContext(),R.color.Scheduled)
                                ))
                            }
                            InProgress->{
                                pieChartModels.add(PieChartModel(
                                    siteStates[i].count.toFloat(),
                                    getTextState(requireContext(),siteStates[i].audit_status),
                                    ContextCompat.getColor(requireContext(),R.color.InProgress)
                                ))
                            }
                            Visited->{
                                pieChartModels.add(PieChartModel(

                                    siteStates[i].count.toFloat(),
                                    getTextState(requireContext(),siteStates[i].audit_status),
                                    ContextCompat.getColor(requireContext(),R.color.Visited)
                                ))

                            }
                            Problematic->{
                                pieChartModels.add(PieChartModel(
                                    siteStates[i].count.toFloat(),
                                    getTextState(requireContext(),siteStates[i].audit_status),
                                    ContextCompat.getColor(requireContext(),R.color.Problematic)
                                ))
                            }
                            Reported->{
                                pieChartModels.add(PieChartModel(
                                    siteStates[i].count.toFloat(),
                                    getTextState(requireContext(),siteStates[i].audit_status),
                                    ContextCompat.getColor(requireContext(),R.color.Reported)
                                ))
                            }

                        }
                    }
                    createPieChart(sharedPref.getString(SELECTED_LANGUAGE)!!,viewBinding.piechartSitestates, pieChartModels, getString(R.string.auditSiteStates), resources.getColor(android.R.color.transparent))
                }
            }
        }

    }

    override fun onShow() {

    }



    override fun initViewModel(): FragmentHomeViewModel {
        val fragmentHomeViewModel : FragmentHomeViewModel by viewModels()
        return fragmentHomeViewModel
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
         * @return A new instance of fragment HomeMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}