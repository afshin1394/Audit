package com.irancell.nwg.ios.presentation.dialog.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.DialogModel
import com.irancell.nwg.ios.databinding.FragmentDoubleActionBinding

import com.irancell.nwg.ios.util.setAnimationClick

/**
 * A simple [Fragment] subclass.
 * Use the [DoubleActionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class DoubleActionFragment(): DialogFragment() {
    // TODO: Rename and change types of parameters
    lateinit var dialogListener : DialogListener

    private var dialogModel: DialogModel? = null
    private var isCancellable : Boolean = true

    private var _binding: FragmentDoubleActionBinding? = null
    private val binding get() = _binding!!




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogModel = it.getParcelable(ARG_PARAM1) as DialogModel?
            isCancellable = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (isCancellable){
            dialog!!.setCancelable(true)
        }else{
            dialog!!.setCancelable(false)
        }
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window
        _binding = FragmentDoubleActionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = dialogModel
//        binding.imgBanner.setAnimation(R.raw.question_mark)

//        dialogModel?.rawRes?.let {
//            binding.imgBanner.setAnimation(R.raw.question_mark)
//        }
        binding.executePendingBindings()

        binding.cardActionOne.setAnimationClick(lifecycleScope, R.anim.button_click){
            dialogListener.onActionOne(this)
        }
        binding.cardActiontwo.setAnimationClick(lifecycleScope, R.anim.button_click){
            dialogListener.onActionTwo(this)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(dialogModel: DialogModel, isCancellable : Boolean) =
            DoubleActionFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, dialogModel)
                    putBoolean(ARG_PARAM2,isCancellable)
                }
            }

    }
    fun  setListener(dialogListener: DialogListener){
        this.dialogListener =  dialogListener
    }
    interface DialogListener{
        fun onActionOne(dialog : DoubleActionFragment)
        fun onActionTwo(dialog : DoubleActionFragment)
    }


}