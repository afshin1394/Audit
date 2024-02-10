package com.irancell.nwg.ios.presentation.audit.Fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.SELECTED_LANGUAGE
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.audit.Detail
import com.irancell.nwg.ios.data.remote.response.audit.Name
import com.irancell.nwg.ios.databinding.FragmentDialogBinding
import com.irancell.nwg.ios.util.SharedPref
import com.irancell.nwg.ios.util.setAnimationClick
import com.irancell.nwg.ios.util.setAnimationDelayedClick
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val ARG_PARAM1 = "title"
private const val ARG_PARAM2 = "image"
private const val ARG_PARAM3 = "detail"

@AndroidEntryPoint
class  DialogFragment() : androidx.fragment.app.DialogFragment() {
    private var title: Name? = null
    private var uri: Uri? = null
    private var detail : Detail? = null

    private var _binding: FragmentDialogBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var sharedPref: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.get(ARG_PARAM1) as Name
            uri = it.getParcelable(ARG_PARAM2)
            detail = it.getParcelable(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDialogBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        this.isCancelable = false
        Glide.with(view.context)
            .load(uri)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(binding.imgPhoto as ImageView)
        binding.details.text = String.format("%1s:%2s %3s:%4s      %5s:%6s",requireContext().resources.getString(R.string.latitude),detail!!.latitude,requireContext().resources.getString(R.string.longitude),detail!!.longitude,requireContext().resources.getString(R.string.date),detail!!.date)
        binding.btnDismiss.setOnClickListener{
            this.dismiss()
        }
        if (sharedPref.getString(SELECTED_LANGUAGE).equals("en")){
            binding.txtAttrName.text=title?.english
        }else{
            binding.txtAttrName.text=title?.persian
        }

        binding.linRotate?.setAnimationDelayedClick(lifecycleScope,R.anim.button_click,500){
            binding.imgPhoto.rotation = binding.imgPhoto.rotation + 90f
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {

        @JvmStatic
        fun newInstance(element : AttrElement) =
            DialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, element.name)
                    putParcelable(ARG_PARAM2,Uri.parse(element.data?.contentUri))
                    putParcelable(ARG_PARAM3,element.data?.details)

                }
            }
    }

}
