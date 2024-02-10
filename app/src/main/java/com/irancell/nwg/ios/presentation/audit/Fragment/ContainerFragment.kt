package com.irancell.nwg.ios.presentation.audit.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.remote.response.audit.Name
import com.irancell.nwg.ios.databinding.FragmentContainerBinding
import com.irancell.nwg.ios.util.constants.MAIN
import com.irancell.nwg.ios.presentation.audit.activity.FormType
import com.irancell.nwg.ios.presentation.audit.activity.GroupID
import com.irancell.nwg.ios.presentation.audit.activity.SiteID
import com.irancell.nwg.ios.presentation.audit.activity.Title
import com.irancell.nwg.ios.presentation.audit.viewModel.ContainerFragmentViewModel
import com.irancell.nwg.ios.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ContainerFragment : BaseFragment<FragmentContainerBinding, ContainerFragmentViewModel>() {
    private var siteId: Int? = null
    private var groupId: String? = null
    private var title : Name? = null
    private var formType : Int? = null



    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentContainerBinding {
        return FragmentContainerBinding.inflate(layoutInflater, container, false)
    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.apply {
            groupId = this.getString(GroupID)
            title = this.get(Title) as Name
            siteId = this.get(SiteID) as Int
            formType = this.getInt(FormType)
        }
        Log.i("initView:", " siteId"+siteId+ " groupId"+ groupId+ " title"+title)
        val auditfragment = AuditFragment.newInstance(siteId!!, groupId!!,title!!, MAIN , formType!!)
        auditfragment.stamp = stamp
         startFragment(
            childFragmentManager,
            auditfragment,
            R.id.audit_fragment_container,
        )
    }

    override fun onShow() {
    }


    override fun initViewModel(): ContainerFragmentViewModel {
        val containerFragmentViewModel: ContainerFragmentViewModel by viewModels()
        return containerFragmentViewModel
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        bundle: Bundle
    ) {
    }


    override fun onDestroyView() {
        super.onDestroyView()

    }
    var stamp : String? = null


    companion object {

        @JvmStatic
        fun newInstance(siteId: Int, groupId: String,title: Name,formType : Int): ContainerFragment {
            return ContainerFragment().apply {
                arguments = Bundle().apply {
                    putInt(SiteID, siteId)
                    putString(GroupID, groupId)
                    putParcelable(Title,title)
                    putInt(FormType ,formType)
                }
            }
        }

    }
}