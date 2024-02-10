package com.irancell.nwg.ios.presentation.main.fragment

import android.app.ActionBar
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.databinding.FragmentAuditMainBinding
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.presentation.main.adapter.ProjectAdapter
import com.irancell.nwg.ios.presentation.main.adapter.RegionListener
import com.irancell.nwg.ios.presentation.main.viewModel.FragmentAuditMainViewModel
import com.irancell.nwg.ios.util.constants.Visited
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.Query

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuditMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class AuditMainFragment : BaseFragment<FragmentAuditMainBinding, FragmentAuditMainViewModel>(),
    RegionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var projectAdapter: ProjectAdapter? = null

    var projectDomainList: ArrayList<ProjectDomain>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }

    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentAuditMainBinding {
        return FragmentAuditMainBinding.inflate(layoutInflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        (requireActivity() as MainActivity).viewBinding.actionBar.txtLoadedPage.text =
            resources.getString(R.string.action_audit)

        viewModel.language.observe(lifecycleOwner){
            it?.let { language->
                this.projectAdapter = ProjectAdapter(this)
                this.projectAdapter?.language = language
                viewBinding.rvRegion.adapter = projectAdapter

//                viewModel.getAllRegion().observe(lifecycleOwner)
//                {
//                    regionAdapter.submitList(it)
//                }
            }
        }




        viewModel.getAllProjects().observe(lifecycleOwner){
            when(it.status){
                AsyncStatus.LOADING->{
                    Log.i("getAllProjects", "initView: loading")
                }
                AsyncStatus.SUCCESS->{
                    projectDomainList = it.data?.let { it1 -> ArrayList(it1) }
                    projectAdapter?.submitList(projectDomainList)
                    Log.i("getAllProjects", "initView: success"+it.data)

                }
                AsyncStatus.ERROR->{
                    Log.i("getAllProjects", "initView: success"+it.code)

                }
            }
        }


    }




    override fun onShow() {
    }

    override fun onSearch(query: String) {
        Log.i("SearchActionBar", "onSearch AuditMainFragment"+query)
        val filtered = projectDomainList?.filter { projectDomain -> projectDomain.name.trim().lowercase().contains(query.trim().lowercase()) || projectDomain.company.trim().lowercase().contains(query.trim().lowercase())  }
        projectAdapter?.submitList(filtered)
    }

    override fun initViewModel(): FragmentAuditMainViewModel {
        return ViewModelProvider(this)[FragmentAuditMainViewModel::class.java]
    }

    override fun initActionBar(): SearchActionBarBinding {
        return (requireActivity() as MainActivity).viewBinding.actionBar
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AuditMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuditMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun requestSite(position: Int, projectDomain: ProjectDomain) {
        navigateWithArg(
            R.id.siteFragment, bundleOf(
                Pair("projectId", projectDomain.id),
                Pair("regionName", projectDomain.name),

            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        projectAdapter = null
    }
}