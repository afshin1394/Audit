package com.irancell.nwg.ios.presentation.audit.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import androidx.work.WorkInfo
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.AuditStateDomain
import com.irancell.nwg.ios.data.model.DialogModel
import com.irancell.nwg.ios.data.model.MandatoryField
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.request.sumbit_audit.SubmitAuditRequest
import com.irancell.nwg.ios.data.remote.response.audit.Name
import com.irancell.nwg.ios.databinding.ActivityAuditBinding
import com.irancell.nwg.ios.network.base.AsyncStatus
import com.irancell.nwg.ios.presentation.audit.Fragment.ContainerFragment
import com.irancell.nwg.ios.presentation.audit.adapter.ViewPagerAdapter
import com.irancell.nwg.ios.presentation.audit.viewModel.AuditViewModel
import com.irancell.nwg.ios.presentation.base.BaseActivity
import com.irancell.nwg.ios.presentation.dialog.fragment.AlarmFragment
import com.irancell.nwg.ios.presentation.dialog.fragment.DoubleActionFragment
import com.irancell.nwg.ios.presentation.dialog.fragment.ListDialogFragment
import com.irancell.nwg.ios.service.SendGpsService
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.constants.*
import com.irancell.nwg.ios.util.constants.ENGLISH
import com.irancell.nwg.ios.util.constants.PERSIAN
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException


const val SiteID = "siteId"
const val GroupID = "groupId"
const val Title = "title"
const val FormType = "FormType"

var init: Boolean = true
lateinit var siteDomain: SiteDomain
var type: Int = 0

@AndroidEntryPoint
class AuditActivity : BaseActivity<ActivityAuditBinding, AuditViewModel>() {

    lateinit var sendGpsIntent: Intent


    override fun initFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun inflateBiding(): ActivityAuditBinding {
        return ActivityAuditBinding.inflate(layoutInflater)
    }


    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun initViews() {


        startGpsService()


        viewModel.language.observe(this@AuditActivity) { language ->
            if (init)
                loading.showLoading()
            intent.extras?.let {
                siteDomain = it.get("siteDomain") as SiteDomain
                type = it.get("type") as Int

                viewBinding.txtRegion.text = siteDomain.region_name
                viewBinding.txtSiteName.text = "(" + siteDomain.site_name + ")"
//                viewBinding.txtSiteCode.text = siteDomain.vendor
            }
            auditImageFolder(this)
            auditRegionFolder(this, siteDomain.region_name)
            auditSiteFolder(this, siteDomain.region_name, siteDomain.site_name)
            viewModel.getAttributeGroups(siteDomain.siteId, type).observe(this) {
                if (init) {
                    it?.let { it ->
                        var fragments: ArrayList<Fragment> = ArrayList()

                        //bind the data to the ui
                        it.iterator().forEach { tabModel ->
                            var containerFragment = ContainerFragment.newInstance(
                                siteDomain.siteId, tabModel.groupId,
                                Name(tabModel.persianName.trim(), tabModel.englishName.trim()),
                                type
                            )
                            containerFragment.stamp = tabModel.englishName
                            fragments.add(containerFragment)
                        }

                        viewBinding.viewPager.adapter =
                            ViewPagerAdapter(fragments, supportFragmentManager, lifecycle)
                        viewBinding.viewPager.isUserInputEnabled = false

                        TabLayoutMediator(
                            viewBinding.tabLayout,
                            viewBinding.viewPager
                        ) { tab, position ->
                            run {

                                when (language) {
                                    ENGLISH -> {
                                        tab.text = it[position].englishName.trim()

                                    }
                                    PERSIAN -> {
                                        tab.text = it[position].persianName.trim()
                                    }
                                }
                            }
                        }.attach()
                        viewBinding.viewPager.registerOnPageChangeCallback(object :
                            OnPageChangeCallback() {
                            override fun onPageScrolled(
                                position: Int,
                                positionOffset: Float,
                                positionOffsetPixels: Int
                            ) {
                                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                            }

                            override fun onPageSelected(position: Int) {
//                                removeState()
//                                addState(State(it[position].siteId,it[position].groupId, "" ,"",it[position].englishName))
                                Log.i("tabState", "onResume: " + getState())
                                super.onPageSelected(position)
                            }

                            override fun onPageScrollStateChanged(state: Int) {
                                super.onPageScrollStateChanged(state)
                            }
                        })
                    }
                    init = false
                    loading.dismissLoading()
                }
            }

            viewBinding.IVUpload.setAnimationClick(lifecycle.coroutineScope, R.anim.button_click) {
                viewModel.getAuditState(siteDomain.siteId).observe(this) {
                    when (it.status) {
                        AsyncStatus.SUCCESS -> {
                            if (it.data == Scheduled || it.data == Reported)
                                openUploadDialog()
                            else
                                openUploadDialog()


                        }
                        AsyncStatus.ERROR -> {
                            showToast(this, R.string.ReUploadImpossible)

                        }
                        AsyncStatus.LOADING -> {

                        }
                    }

                }


            }

        }

    }

    fun startGpsService() {
        Log.i("isRunning", "stopGpsService: "+SendGpsService.isRunning)

        sendGpsIntent = Intent(this, SendGpsService::class.java)
        if (!SendGpsService.isRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(sendGpsIntent)
            } else {
                startService(sendGpsIntent)
            }
        }
        Log.i("isRunning", "stopGpsService: "+SendGpsService.isRunning)

    }


    fun stopGpsService() {
        if (SendGpsService.isRunning)
            stopService(sendGpsIntent)
        Log.i("isRunning", "stopGpsService: "+SendGpsService.isRunning)
    }

    private fun openUploadDialog() {
        val dialogModel = DialogModel(
            getString(R.string.upload_modification),
            getString(R.string.yes),
            getString(R.string.no),
            "#FFBE00",
            "#FFBE00",
            null
        )
        val doubleActionFragment = DoubleActionFragment.newInstance(dialogModel, true)
        doubleActionFragment.setListener(object : DoubleActionFragment.DialogListener {
            override fun onActionOne(dialog: DoubleActionFragment) {
                dialog.dismiss()
                checkForMandatoryFields(siteDomain.siteId)
            }

            override fun onActionTwo(dialog: DoubleActionFragment) {
                dialog.dismiss()
            }
        })

        startDialogFragment(doubleActionFragment)
    }

    private fun checkForMandatoryFields(siteId: Int) {
        viewModel.checkForMandatoryFields(siteId, type).observe(this) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    if (it.data?.isEmpty() == false) {
                        showMandatoryListDialog(it.data)
                        loading.dismissLoading()
                    } else {
                        getAuditPerSite(siteDomain.siteId, siteDomain.audit_id)
                    }
                }
                AsyncStatus.ERROR -> {
                    Log.i("sdadadadd", "checkForMandatoryFields: " + it.message)

                    loading.dismissLoading()
                }
                AsyncStatus.LOADING -> {
                    loading.showLoading()
                }
            }
        }

    }


    override fun onDestroy() {
        init = true
        super.onDestroy()
//        stopService(sendGpsIntent)
    }

    override fun initViewModel(): AuditViewModel {
        return ViewModelProvider(this)[AuditViewModel::class.java]
    }

    private fun showMandatoryListDialog(mandatories: List<MandatoryField>) {
        Log.i("Mandatriesss", "showMandatoryListDialog: " + mandatories)
        viewModel.language.observe(this) {


            val dialogModel = DialogModel(
                this.getString(R.string.please_complete_following),
                this.getString(R.string.dismiss),
                this.getString(R.string.delete),
                "#FFBE00",
                "#FFBE00",
                null,
                list = mandatories.map { mandatory -> mandatory.name }
            )
            val listDialogFragment =
                ListDialogFragment.newInstance(dialogModel, false)
            it?.let {
                listDialogFragment.language = it
            }
            listDialogFragment.setListener(object : ListDialogFragment.DialogListener {
                override fun onActionOne(dialog: ListDialogFragment) {
                    dialog.dismiss()
                }

            })

            startDialogFragment(listDialogFragment)
        }
    }

    private fun getAuditPerSite(siteId: Int, auditId: Int) {
        viewModel.getAuditPerSite(siteId = siteId, type).observe(this) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    it.data?.let { submitAuditRequest ->
                        submitAudit(auditId, submitAuditRequest)
//                        loading.dismissLoading()
                    } ?: {
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
        }

    }

    private fun submitAudit(auditId: Int, data: SubmitAuditRequest) {

        Log.i("submitAudit", "submitAudit: " + Gson().toJson(data))
        viewModel.submitAudit(auditId, data, type).observe(this@AuditActivity) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    uploadAudit(siteDomain)
                    Log.i("submitAudit", "submitAudit: successfully send  ")
                }
                AsyncStatus.ERROR -> {
                    Log.i("submitAudit", "submitAudit: ERROR send  " + it.message)
                    loading.dismissLoading()
                }
                AsyncStatus.LOADING -> {

                }
            }
        }
    }


    private fun uploadAudit(siteDomain: SiteDomain) {
        val fileDirPath = this@AuditActivity.filesDir.path
        viewModel.uploadAudit(fileDirPath, siteDomain, type).observe(this@AuditActivity) {
            when (it.state) {
                WorkInfo.State.SUCCEEDED -> {
                    Log.i("uploadAudit", "uploadAudit: SUCCESS")
                    updateState(siteDomain.audit_id)
                }
                WorkInfo.State.FAILED -> {
                    Log.i("uploadAudit", "uploadAudit: ERROR")
                    loading.dismissLoading()
                }
                WorkInfo.State.ENQUEUED -> {
                    Log.i("uploadAudit", "uploadAudit: LOADING")
                }
                WorkInfo.State.RUNNING -> {
                    Log.i("uploadAudit", "uploadAudit: RUNNING")
                }
                WorkInfo.State.BLOCKED -> {
                    Log.i("uploadAudit", "uploadAudit: BLOCKED")

                }
                WorkInfo.State.CANCELLED -> {
                    Log.i("uploadAudit", "uploadAudit: CANCELLED")

                }
            }
        }
    }

    private fun updateState(auditId: Int) {
        viewModel.getSubmittedAuditState(audit_id = auditId).observe(this@AuditActivity) {
            when (it.status) {
                AsyncStatus.SUCCESS -> {
                    it.data?.let { data ->
                        updateSiteAuditState(data)
                    } ?: run {
                        loading.dismissLoading()
                    }

                }
                AsyncStatus.ERROR -> {
                    loading.dismissLoading()

                }
                AsyncStatus.LOADING -> {


                }
            }
        }


    }

    private fun updateSiteAuditState(auditStateDomain: AuditStateDomain) {
        viewModel.updateSiteByAuditId(auditStateDomain.audit_id, auditStateDomain.state)
        startSuccessAlarm()
        loading.dismissLoading()
    }


    private fun startSuccessAlarm() {
        val dialogModel = DialogModel(
            resources.getString(R.string.successUpload),
            resources.getString(R.string.confirm),
            "",
            "#FFBE00",
            "#FFBE00",
            null,
            R.raw.success
        )

        val alarmFragment = AlarmFragment.newInstance(dialogModel, true)
        alarmFragment.setListener(object : AlarmFragment.DialogListener {
            override fun onActionOne(dialog: AlarmFragment) {
                dialog.dismiss()
            }
        })
        startDialogFragment(alarmFragment)
    }


}


