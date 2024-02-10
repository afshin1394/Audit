package com.irancell.nwg.ios.presentation.dialog.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.DialogModel
import com.irancell.nwg.ios.databinding.FragmentAlarmBinding
import com.irancell.nwg.ios.util.setAnimationClick

/**
 * A simple [Fragment] subclass.
 * Use the [AlarmFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class AlarmFragment : DialogFragment() {
    lateinit var dialogListener : DialogListener

    private var dialogModel: DialogModel? = null
    private var isCancellable : Boolean = true

    private var _binding: FragmentAlarmBinding? = null
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
        _binding = FragmentAlarmBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.data = dialogModel
        binding.executePendingBindings()
        binding.lottieAlarm.setAnimation(R.raw.success)
        binding.cardActionOne.setAnimationClick(lifecycleScope, R.anim.button_click){
            dialogListener.onActionOne(this)
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(dialogModel: DialogModel, isCancellable : Boolean) =
            AlarmFragment().apply {
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
        fun onActionOne(dialog : AlarmFragment)
    }

}