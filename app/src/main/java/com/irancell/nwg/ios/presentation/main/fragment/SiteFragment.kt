package com.irancell.nwg.ios.presentation.main.fragment

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.databinding.FragmentSiteBinding
import com.irancell.nwg.ios.databinding.SearchActionBarBinding
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.presentation.audit.activity.AuditActivity
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.dialog.fragment.ProgressBarFragment
import com.irancell.nwg.ios.presentation.main.MainActivity
import com.irancell.nwg.ios.presentation.main.adapter.SiteAdapter
import com.irancell.nwg.ios.presentation.main.adapter.SiteClickListener
import com.irancell.nwg.ios.presentation.main.viewModel.FragmentSiteViewModel
import com.irancell.nwg.ios.util.constants.*
import com.irancell.nwg.ios.util.doesPackageExisted
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SiteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SiteFragment : BaseFragment<FragmentSiteBinding, FragmentSiteViewModel>(), SiteClickListener {

    lateinit var siteAdapter: SiteAdapter
    lateinit var siteDomain: SiteDomain
    var siteDomainList: ArrayList<SiteDomain>? = null
    var searchedValue: String = ""
    var gps : Location? = null

    private val progressBarFragment by lazy {
        ProgressBarFragment.newInstance()
    }


    override fun inflateBiding(
        inflater: LayoutInflater?, container: ViewGroup?
    ): FragmentSiteBinding {
        return FragmentSiteBinding.inflate(layoutInflater, container, false)
    }

    override fun initActionBar(): SearchActionBarBinding {
        return (requireActivity() as MainActivity).viewBinding.actionBar
    }

    override fun initView(savedInstanceState: Bundle?) {
        viewModel.location.observe(lifecycleOwner) {
            it?.let { location ->
                gps = location
            }
        }


        viewModel.language.observe(lifecycleOwner) {
            it?.let { language ->
                siteAdapter = SiteAdapter(lifecycleScope, this)
                siteAdapter.language = language
                viewBinding.rvSite.adapter = siteAdapter
                setAuditStateFilters()
            }
        }

    }



    private fun getSites(projectId: Int) {
        viewModel.getSitesByProjectId(projectId = projectId).observe(lifecycleOwner) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    siteDomainList = it.data?.let { it1 -> ArrayList(it1) }
                    viewModel.filterState.observe(lifecycleOwner) { filter ->

                        val result = if (filter == All) {
                            siteDomainList
                        } else {
                            siteDomainList?.filter { siteDomain -> siteDomain.audit_status == filter }
                        }

                        result?.let {
                            siteAdapter.submitList(result)
                        }
                        loading.dismissLoading()
                    }
                }
                AsyncStatus.ERROR -> {
                    loading.dismissLoading()
                }
                AsyncStatus.LOADING -> {
                    loading.showLoading()
                }
            }

            Log.i("Sitefragment", "getSites: ")
        }

    }


    private fun setAuditStateFilters() {
        viewModel.filterState.value = All


        viewBinding.all.tvChips.setText(R.string.all)
        viewBinding.scheduled.tvChips.setText(R.string.Scheduled)
        viewBinding.inProgress.tvChips.setText(R.string.InProgress)
        viewBinding.visited.tvChips.setText(R.string.Visited)
        viewBinding.rejected.tvChips.setText(R.string.Rejected)
        viewBinding.problematic.tvChips.setText(R.string.problematic)
        viewBinding.reported.tvChips.setText(R.string.Reported)

        val linFilters = arrayListOf(
            viewBinding.all.linChips,
            viewBinding.scheduled.linChips,
            viewBinding.inProgress.linChips,
            viewBinding.inProgress.linChips,
            viewBinding.visited.linChips,
            viewBinding.rejected.linChips,
            viewBinding.problematic.linChips,
            viewBinding.reported.linChips
        )
        disableAllViewsExcept(linFilters, viewBinding.all.linChips)
        viewBinding.all.linChips.setOnClickListener {
            viewModel.filterState.value = All

            disableAllViewsExcept(linFilters, viewBinding.all.linChips)
            if (searchedValue.isNotEmpty()) {
                siteAdapter.submitList(siteDomainList!!.filter { siteDomain -> siteDomain.site_name.trim().lowercase().contains(searchedValue.trim().lowercase()) || siteDomain.province.trim().lowercase().contains(searchedValue.trim().lowercase()) })
            }else{
                siteAdapter.submitList(siteDomainList!!)
            }


        }

        viewBinding.scheduled.linChips.setOnClickListener {
            viewModel.filterState.value = Scheduled
            disableAllViewsExcept(linFilters, viewBinding.scheduled.linChips)
            setFiltered(Scheduled)


        }
        viewBinding.inProgress.linChips.setOnClickListener {
            viewModel.filterState.value = InProgress

            disableAllViewsExcept(linFilters, viewBinding.inProgress.linChips)

            setFiltered(InProgress)
        }
        viewBinding.visited.linChips.setOnClickListener {
            viewModel.filterState.value = Visited


            disableAllViewsExcept(linFilters, viewBinding.visited.linChips)


            setFiltered(Visited)

        }

        viewBinding.rejected.linChips.setOnClickListener {
            viewModel.filterState.value = Rejected


            disableAllViewsExcept(linFilters, viewBinding.rejected.linChips)


            setFiltered(Rejected)

        }
        viewBinding.problematic.linChips.setOnClickListener {
            viewModel.filterState.value = Problematic


            disableAllViewsExcept(linFilters, viewBinding.problematic.linChips)


            setFiltered(Problematic)

        }

        viewBinding.reported.linChips.setOnClickListener {
            viewModel.filterState.value = Reported


            disableAllViewsExcept(linFilters, viewBinding.reported.linChips)


            setFiltered(Reported)

        }
    }

    private fun setFiltered(state: Int) {
        Log.i("searchedvalue", "setFiltered: "+searchedValue)
        lifecycleScope.launch(Dispatchers.IO) {
            if(searchedValue.isNotEmpty()) {
                val filtered = siteDomainList?.filter { siteDomain ->
                    siteDomain.audit_status == state && (siteDomain.site_name.trim().lowercase().contains(searchedValue.trim().lowercase()) || siteDomain.province.trim().lowercase().contains(searchedValue.trim().lowercase()))
                }
                Log.i("searchedvalue", "setFiltered: "+filtered)

                siteAdapter.submitList(filtered!!)
            }else{
                val filtered = siteDomainList?.filter { siteDomain ->
                    siteDomain.audit_status == state
                }
                siteAdapter.submitList(filtered!!)
            }

        }
    }

    private fun disableAllViewsExcept(
        linFilters: ArrayList<LinearLayout>, lin_chips: LinearLayout
    ) {
        linFilters.forEach {
            it.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        }
        lin_chips.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.colorPrimary)

    }


    override fun onShow() {
        arguments?.let { it ->
            it.getString("regionName")
            (requireActivity() as MainActivity).viewBinding.actionBar.txtLoadedPage.text =
                it.getString("regionName")
            lifecycleScope.launchWhenResumed {
                delay(50)
                getSites(it.getInt("projectId"))
            }
            /**Update Process observers*/
        }
    }

    override fun onSearch(query: String) {
        searchedValue = query
        Log.i("SearchActionBar", "onSearch SiteFragment" + query)
        if (query.isNotEmpty()) {
            if (viewModel.filterState.value == All) {
                val filtered = siteDomainList?.filter { siteDomain ->
                    siteDomain.site_name.trim().lowercase().contains(query.trim().lowercase()) || siteDomain.province.trim().lowercase().contains(
                        query.trim().lowercase()
                    )
                }
                siteAdapter.submitList(filtered!!)
            } else {
                val filtered =
                    siteDomainList?.filter { siteDomain -> siteDomain.audit_status == viewModel.filterState.value }
                val filtered2 = filtered?.filter { siteDomain ->
                    siteDomain.site_name.trim().lowercase().contains(query.trim().lowercase()) || siteDomain.province.trim().lowercase().contains(
                        query.trim().lowercase()
                    )
                }
                siteAdapter.submitList(filtered2!!)

            }

        } else {
            if (viewModel.filterState.value == All)
                siteAdapter.submitList(siteDomainList!!)
            else
                setFiltered(viewModel.filterState.value!!)

        }
    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }

    override fun initViewModel(): FragmentSiteViewModel {
        return ViewModelProvider(this)[FragmentSiteViewModel::class.java]
    }

    override fun onBackPressed() {
        navigate(R.id.auditMainFragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SiteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = SiteFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }

    override fun onSiteClick(position: Int, site: SiteDomain, action: Int) {
        siteDomain = site
        when (action) {
            Routing -> {
                gps?.let {

                        val navigation: Uri =
                            Uri.parse("google.navigation:q=${siteDomain.latitude},${siteDomain.longitude}")
                        val navigationIntent = Intent(Intent.ACTION_VIEW, navigation)
//                        navigationIntent.setPackage("com.google.android.apps.maps")
                        startActivity(navigationIntent)

                }?:run{
                    showToast(requireContext(),R.string.gps_signal_lost)
                }
            }
            Audit -> {
                gps?.let {
                    startActivity(
                        Intent(
                            this@SiteFragment.context,
                            AuditActivity::class.java
                        ).apply {
                            this.putExtra("siteDomain", siteDomain)
                            this.putExtra("type", MAIN)
                        })
                }?:run{
                    showToast(requireContext(),R.string.gps_signal_lost)
                }
            }
            ProblemReport -> {
                startActivity(Intent(this@SiteFragment.context, AuditActivity::class.java).apply {
                    this.putExtra("siteDomain", siteDomain)
                    this.putExtra("type", PROBLEMATIC)
                })
            }
        }


    }


}


