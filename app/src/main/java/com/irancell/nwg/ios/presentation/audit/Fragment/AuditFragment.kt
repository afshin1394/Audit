package com.irancell.nwg.ios.presentation.audit.Fragment


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider.getUriForFile
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.*
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.DialogModel
import com.irancell.nwg.ios.data.remote.response.audit.*
import com.irancell.nwg.ios.databinding.FragmentAuditBinding
import com.irancell.nwg.ios.presentation.audit.*
import com.irancell.nwg.ios.presentation.audit.activity.*
import com.irancell.nwg.ios.presentation.audit.adapter.SubGroupAdapter
import com.irancell.nwg.ios.presentation.audit.adapter.SubGroupListener
import com.irancell.nwg.ios.presentation.audit.viewModel.AuditFragmentViewModel
import com.irancell.nwg.ios.presentation.barcode.BARCODE_CONTENT
import com.irancell.nwg.ios.presentation.barcode.BarcodeActivity
import com.irancell.nwg.ios.presentation.base.BaseFragment
import com.irancell.nwg.ios.presentation.dialog.fragment.AlarmFragment
import com.irancell.nwg.ios.presentation.dialog.fragment.DoubleActionFragment
import com.irancell.nwg.ios.presentation.dialog.fragment.DoubleActionFragment.DialogListener
import com.irancell.nwg.ios.service.SendGpsService
import com.irancell.nwg.ios.util.*
import com.irancell.nwg.ios.util.State
import com.irancell.nwg.ios.util.constants.*
import com.irancell.nwg.ios.util.toast.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


private const val FragmentType = "FragmentType"

@AndroidEntryPoint
class AuditFragment() : BaseFragment<FragmentAuditBinding, AuditFragmentViewModel>(),
    SubGroupListener {
    private var siteId: Int? = null
    private var groupId: String? = null
    private var title: Name? = null
    private var fragmentType: Int = -1
    private var formType: Int = -1


    lateinit var adapter: SubGroupAdapter

    var gps: android.location.Location? = null

    var imageUri: Uri? = null
    var newFile: File? = null
    private var imageUUID: String? = null

    var saved = false
    var initial = true
    var subGroupPosition: Int = -1
    var elementPosition: Int = -1
    var isGenerator = false


    //    var newFile : File? = null
    override fun inflateBiding(
        inflater: LayoutInflater?,
        container: ViewGroup?
    ): FragmentAuditBinding {
        return FragmentAuditBinding.inflate(layoutInflater, container, false)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AuditActivity).viewBinding.IVBack.setAnimationClick(
            lifecycleScope,
            R.anim.button_click
        ) {
            removeState()
            goBack()
        }

        if (fragmentType == INNER) {
            (requireActivity() as AuditActivity).viewBinding.tabLayout.visibility = View.GONE

        } else {
            (requireActivity() as AuditActivity).viewBinding.tabLayout.visibility = View.VISIBLE
        }

        (requireActivity() as AuditActivity).viewBinding.IVSave.setAnimationClick(
            lifecycleScope,
            R.anim.button_click
        ) {

            var dialogModel = DialogModel(
                requireContext().getString(R.string.save_modification),
                requireContext().getString(R.string.yes),
                requireContext().getString(R.string.no),
                "#FFBE00",
                "#FFBE00",
                null,R.raw.question_mark
            )
            var doubleActionFragment = DoubleActionFragment.newInstance(dialogModel, true)
            doubleActionFragment.setListener(object : DialogListener {
                override fun onActionOne(dialog: DoubleActionFragment) {
                    dialog.dismiss()
                    lifecycleScope.launch {
                        loading.showLoading()
                        saved = true
                        (requireActivity() as AuditActivity).stopGpsService()
                        viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)
                    }
                }

                override fun onActionTwo(dialog: DoubleActionFragment) {
                    dialog.dismiss()
                }
            })

            startDialogFragment(doubleActionFragment)
        }
        viewModel.language.observe(lifecycleOwner) { language ->
            title?.let {
                language?.let { language ->

                    when (language) {
                        com.irancell.nwg.ios.util.constants.ENGLISH -> {
                            (requireActivity() as AuditActivity).viewBinding.txtDetails.text =
                                it.english

                        }
                        com.irancell.nwg.ios.util.constants.PERSIAN -> {
                            (requireActivity() as AuditActivity).viewBinding.txtDetails.text =
                                it.persian

                        }
                    }
                    Log.i("title", "initView: " + title)
                }
            }

        }


    }

    override fun initFragmentManager(): FragmentManager {
        return parentFragmentManager
    }

    @SuppressLint("SuspiciousIndentation")
    override fun initView(savedInstanceState: Bundle?) {
        viewModel.location.observe(lifecycleOwner) {
            it?.let { location ->
                gps = location
            }
        }

        savedInstanceState?.let {
            if (imageUri == null) {
                imageUri = Uri.parse(savedInstanceState.getString("ImageUri"))
            }
        }

        arguments?.let {
            siteId = it.getInt(SiteID)
            groupId = it.getString(GroupID)
            title = it.getParcelable(Title)
            fragmentType = it.getInt(FragmentType)
            formType = it.getInt(FormType)
        }

        viewModel.language.observe(lifecycleOwner) {
            this.adapter = SubGroupAdapter(lifecycleScope, this, it!!)
            viewBinding.rvSubGroups.itemAnimator = null
            viewModel.getAttributesByGroupId(siteId, groupId!!, formType).observe(this) { group ->
                lifecycleScope.launchWhenResumed {
                    if (initial) {
                        delay(10)
                        this@AuditFragment.adapter.subGroups = group.subGroups
                        viewBinding.rvSubGroups.adapter = adapter
                        initial = false
                    } else if (this@AuditFragment.adapter.subGroups.size == 0) {
                        this@AuditFragment.adapter.subGroups = group.subGroups
                        viewBinding.rvSubGroups.adapter = adapter
                    } else if (isGenerator) {
                        this@AuditFragment.adapter.subGroups = group.subGroups
                        viewBinding.rvSubGroups.adapter = adapter
                        isGenerator = false
                        scrollProperly()
                    } else if (saved) {
                        startSuccessAlarm()
                        loading.dismissLoading()
                        saved = false
                    }
                }
            }
        }
    }

    private fun scrollProperly() {
        viewBinding.rvSubGroups.scrollToPosition(subGroupPosition)
    }

    override fun onShow() {

    }




    override fun initViewModel(): AuditFragmentViewModel {
        return ViewModelProvider(this)[AuditFragmentViewModel::class.java]
    }




    private fun notifyChild() {
        if (adapter.subGroups[subGroupPosition].isGenerated) {
            val subGroupViewHolder =
                viewBinding.rvSubGroups.findViewHolderForAdapterPosition(
                    subGroupPosition
                ) as SubGroupAdapter.ViewHolder

            val childAdapter = subGroupViewHolder.binding.RVElements.adapter
            childAdapter!!.notifyItemChanged(elementPosition)
        } else {

            val subGroupViewHolder =
                viewBinding.rvSubGroups.findViewHolderForAdapterPosition(
                    subGroupPosition
                ) as SubGroupAdapter.ViewHolder
            val childAdapter = subGroupViewHolder.binding.RVElements.adapter
            childAdapter!!.notifyItemChanged(elementPosition)
        }

    }

    override fun onEvent(
        subGroupPosition: Int,
        attrElementPosition: Int,
        innergroupPosition: Int?,
        action: Int
    ) {
        gps?.let {

            if (subGroupPosition != -1 && attrElementPosition != -1) {
                this.subGroupPosition = subGroupPosition
                this.elementPosition = attrElementPosition

                viewModel.subGroup = adapter.subGroups[subGroupPosition]
                viewModel.element =
                    adapter.subGroups[subGroupPosition].element!![attrElementPosition]




                when (action) {
                    SwitchOn -> {
                        groupId?.let { groupId ->
                            siteId?.let { siteId ->
                                viewModel.generate(
                                    siteId,
                                    viewModel.element,
                                    viewModel.subGroup,
                                    groupId
                                )
                                isGenerator = true
                            }
                        }
                    }
                    SwitchOff -> {
                        groupId?.let { groupId ->
                            siteId?.let { siteId ->
                                viewModel.remove(
                                    siteId,
                                    viewModel.element,
                                    viewModel.subGroup,
                                    groupId
                                )
                                isGenerator = true
                            }
                        }

                    }
                    GetLocation -> {
                        addDetails()
                        notifyChild()
                    }


                    OpenInnerGroup -> {

                        innergroupPosition?.let { position ->
                            viewModel.element.group?.let { innerGroups ->

                                addState(
                                    State(
                                        siteId!!,
                                        this.subGroupPosition,
                                        this.elementPosition,
                                        innergroupPosition
                                    )
                                )


                                startFragment(
                                    parentFragmentManager,
                                    newInstance(
                                        siteId!!,
                                        innerGroups[position].id,
                                        innerGroups[position].name,
                                        INNER,
                                        formType
                                    ),
                                    R.id.audit_fragment_container,
                                )
                            }
                        }
                    }
                    Generate -> {

                        groupId?.let { groupId ->
                            siteId?.let { siteId ->
                                viewModel.generate(
                                    siteId,
                                    viewModel.element,
                                    viewModel.subGroup,
                                    groupId
                                )
                                isGenerator = true
                            }
                        }
                    }

                    Remove -> {

                        groupId?.let { groupId ->
                            siteId?.let { siteId ->
                                viewModel.remove(
                                    siteId,
                                    viewModel.element,
                                    viewModel.subGroup,
                                    groupId
                                )
                                isGenerator = true
                            }
                        }
                    }

                    ChoiceSelect -> {

                        addDetails()

                        notifyChild()

                        viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)

                    }

                    Pick -> {
                        addDetails()
                        viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)

                    }

                    TakeImage -> {


                        imageUUID = UUID.randomUUID().toString()
                        val auditImageSubFolder =
                            auditGroupFolder(
                                requireContext(),
                                siteDomain.region_name,
                                siteDomain.site_name
                            )
                        Log.i("package", "onEvent: " + auditImageSubFolder)
                        newFile = File(auditImageSubFolder, imageUUID + ".jpg")
                        imageUri = getUriForFile(
                            requireContext(),
                            "com.irancell.nwg.ios" + ".provider",
                            newFile!!
                        )

                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startForResultsExtras(
                            TakeImage,
                            intent,
                            bundleOf(
                                Pair("attrElement", viewModel.element),
                                Pair("subGroup", viewModel.subGroup)
                            )
                        )
                    }
                    ScanCode -> {
                        startForResultsExtras(
                            ScanCode,
                            Intent(requireActivity(), BarcodeActivity::class.java),
                            bundleOf(
                                Pair("attrElement", viewModel.element),
                                Pair("subGroup", viewModel.subGroup)
                            )
                        )
                    }
                    TextChange -> {
                        addDetails()
                        viewModel.updateAttrs(
                            siteId!!,
                            groupId!!,
                            adapter.subGroups,
                            formType
                        )
                    }

                    PickImage -> {
                        imageUUID = UUID.randomUUID().toString()
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.addCategory(Intent.CATEGORY_OPENABLE)
                        intent.type = "image/*"


                        startForResultsExtras(
                            PickImage,
                            Intent.createChooser(
                                intent,
                                "Select Picture"
                            ),
                            bundleOf(
                                Pair("attrElement", viewModel.element),
                                Pair("subGroup", viewModel.subGroup)
                            )
                        )
                    }
                    PreviewDeleteImage -> {


                        val dialogModel = DialogModel(
                            requireContext().getString(R.string.imageAvailable),
                            requireContext().getString(R.string.preview),
                            requireContext().getString(R.string.delete),
                            "#FFBE00",
                            "#FFBE00",
                            null
                        )
                        var doubleActionFragment =
                            DoubleActionFragment.newInstance(dialogModel, true)
                        doubleActionFragment.setListener(object : DialogListener {
                            override fun onActionOne(dialog: DoubleActionFragment) {
                                dialog.dismiss()
                                startDialogFragment(
                                    DialogFragment.newInstance(
                                        viewModel.element
                                    )
                                )
                            }

                            override fun onActionTwo(dialog: DoubleActionFragment) {
                                val fileName =
                                    "$groupId" + "${viewModel.subGroup.id}" + "${viewModel.element.id}"
                                val imagesFolder = File(
                                    context?.filesDir,
                                    "AuditImages" + "/" + siteDomain.region_name + "/" + siteDomain.site_name
                                )
                                var file = File(imagesFolder, viewModel.element.id + ".jpg")
                                if (file.exists())
                                    file.delete()

                                dialog.dismiss()
                                adapter.subGroups[subGroupPosition].element!![attrElementPosition].data =
                                    Data(null)

                                notifyChild()

                                viewModel.updateAttrs(
                                    siteId!!,
                                    groupId!!,
                                    adapter.subGroups,
                                    formType
                                )

                            }
                        })

                        startDialogFragment(doubleActionFragment)


                    }

                }


            }
        } ?: showToast(requireContext(), R.string.gps_signal_lost)
    }

    private fun addDetails() {
        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")
        gps?.let { gps ->
            val dateStamp =
                gps.time.getDateFromMillis(sdf)
            val attrElement =
                adapter.subGroups[subGroupPosition].element!![elementPosition]
            if (attrElement.data?.details != null) {
                attrElement.data?.details?.latitude =
                    gps.latitude.toString()
                attrElement.data?.details?.longitude =
                    gps.longitude.toString()
                attrElement.data?.details?.date = dateStamp
            } else {
                attrElement.data?.details = Detail(
                    dateStamp,
                    gps.latitude.toString(),
                    gps.longitude.toString()
                )
            }
        } ?: showToast(requireContext(), R.string.gps_signal_lost)
    }

    private fun goBack() {
        viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)
        if (parentFragmentManager.backStackEntryCount > 1) {
            parentFragmentManager.popBackStackImmediate()
        } else {

            val dialogModel = DialogModel(
                getString(R.string.exitAudit),
                requireContext().getString(R.string.yes),
                requireContext().getString(R.string.no),
                "#FFBE00",
                "#FFBE00",
                null
            )
            val doubleActionFragment = DoubleActionFragment.newInstance(dialogModel, true)
            doubleActionFragment.setListener(object : DialogListener {
                override fun onActionOne(dialog: DoubleActionFragment) {
                    dialog.dismiss()
                    requireActivity().finish()
                }

                override fun onActionTwo(dialog: DoubleActionFragment) {
                    dialog.dismiss()
                }
            })
            startDialogFragment(doubleActionFragment)
        }
    }

    override fun onBackPressed() {
        removeState()
        goBack()
    }

    var stamp: String? = null

    companion object {

        @JvmStatic
        fun newInstance(
            siteId: Int,
            groupId: String,
            title: Name,
            fragmentType: Int,
            formType: Int
        ): AuditFragment {
            return AuditFragment().apply {
                arguments = Bundle().apply {
                    putInt(SiteID, siteId)
                    putString(GroupID, groupId)
                    putParcelable(Title, title)
                    putInt(FragmentType, fragmentType)
                    putInt(FormType, formType)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("ImageUri", imageUri.toString())

    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            imageUri = Uri.parse(savedInstanceState.getString("ImageUri"))

        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        bundle: Bundle
    ) {
        gps?.let { gps ->
            if (resultCode == RESULT_OK) {
                val subgroupIndex = findIndex(adapter.subGroups, viewModel.subGroup.id)
                val elementIndex =
                    findIndex(adapter.subGroups[subgroupIndex].element, viewModel.element.id)
                val attrElement = adapter.subGroups[subgroupIndex].element!![elementIndex]
                val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")

                val dateStamp =
                    gps.time.getDateFromMillis(sdf)
                when (requestCode) {

                    TakeImage -> {

                        val fileName =
                            "${viewModel.element.name.english} " + "(" + "$groupId" + "${viewModel.subGroup.id}" + "${viewModel.element.id}" + ")"
                        if (newFile!!.exists()) {
                            viewModel.compressFile(newFile!!.path, newFile!!.path)

                            Log.i("attrElement", "1: " + attrElement)
                            if (attrElement.data != null) {
                                attrElement.data?.contentUri = imageUri.toString()
                                attrElement.data?.Image = imageUUID
                                attrElement.data
                                attrElement.data?.details = Detail(
                                    dateStamp,
                                    gps.latitude.toString(),
                                    gps.longitude.toString()
                                )
                                attrElement.data?.is_closed = false
                                attrElement.data?.is_blocked = false
                                attrElement.data?.is_edited = false
                                attrElement.data?.is_remote_e_tilt = false
                                attrElement.data?.label_image = ""
                            } else {
                                val dataModel = Data(
                                    Detail(

                                        gps.time.toString(),
                                        gps.latitude.toString(),
                                        gps.longitude.toString()
                                    )
                                )
                                dataModel.is_closed = false
                                dataModel.is_blocked = false
                                dataModel.is_edited = false
                                dataModel.is_remote_e_tilt = false
                                dataModel.label_image = ""
                                dataModel.contentUri = imageUri.toString()
                                attrElement.data = dataModel
                            }

                            Log.i("attrElement", "2: " + attrElement)

                            adapter.subGroups[subgroupIndex].element!![elementIndex] =
                                attrElement

                            notifyChild()
                            viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)
                        }


                    }
                    ScanCode -> {

                        data?.let {
                            if (attrElement.data != null) {
                                attrElement.data?.value = it.getStringExtra(BARCODE_CONTENT)
                            } else {
                                attrElement.data = Data(
                                    Detail(

                                        gps.time.toString(),
                                        gps.latitude.toString(),
                                        gps.longitude.toString()
                                    )
                                )
                            }
                        }
                        adapter.subGroups[subgroupIndex].element!![elementIndex] =
                            attrElement

                        notifyChild()
                        viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)


                    }
                    PickImage -> {
                        val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm:ss")


                        val dateStamp =
                            gps.time.getDateFromMillis(sdf)
                        val selectedImageUri = data!!.data
                        val fileName =
                            "${viewModel.element.name.english} " + "(" + "$groupId" + "${viewModel.subGroup.id}" + "${viewModel.element.id}" + ")"


                        val auditImageSubFolder =
                            auditGroupFolder(
                                requireContext(),
                                siteDomain.region_name,
                                siteDomain.site_name
                            )
                        Log.i("package", "onEvent: " + auditImageSubFolder)
                        newFile = File(auditImageSubFolder, imageUUID + ".jpg")
//                                newFile = compressFile(Compress(newFile!!.path,newFile!!.path,1200,960,90))
                        imageUri = getUriForFile(
                            requireContext(),
                            "com.irancell.nwg.ios" + ".provider",
                            newFile!!
                        )


                        val realUri: Uri =
                            getRealPathFromURI(requireContext(), selectedImageUri!!)!!
                        realUri.let { uri ->
                            saveFile(uri, Uri.parse(newFile?.path)!!)
                            if (attrElement.data != null) {
                                attrElement.data?.contentUri = imageUri.toString()
                                attrElement.data?.Image = imageUUID
                                attrElement.data?.details = Detail(
                                    dateStamp,
                                    gps.latitude.toString(),
                                    gps.longitude.toString()
                                )
                                attrElement.data?.is_closed = false
                                attrElement.data?.is_blocked = false
                                attrElement.data?.is_edited = false
                                attrElement.data?.is_remote_e_tilt = false
                                attrElement.data?.label_image = ""
                            } else {
                                val dataModel = Data(
                                    Detail(

                                        gps.time.toString(),
                                        gps.latitude.toString(),
                                        gps.longitude.toString()
                                    )
                                )
                                dataModel.is_closed = false
                                dataModel.is_blocked = false
                                dataModel.is_edited = false
                                dataModel.is_remote_e_tilt = false
                                dataModel.label_image = ""
                                dataModel.contentUri = imageUri.toString()
                                attrElement.data = dataModel
                            }


                            adapter.subGroups[subgroupIndex].element!![elementIndex] =
                                attrElement

                            notifyChild()
                            viewModel.updateAttrs(siteId!!, groupId!!, adapter.subGroups, formType)

                        }


                    }
                }
            }

        } ?: showToast(requireContext(), R.string.gps_signal_lost)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("AuditFragment", "onDestroy:AuditFragment ")

    }

    private fun startSuccessAlarm() {
        val dialogModel = DialogModel(
            resources.getString(R.string.successSave),
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