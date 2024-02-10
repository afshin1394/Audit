package com.irancell.nwg.ios.presentation.dialog.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.irancell.nwg.ios.databinding.FragmentProgressBarBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ProgressBarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProgressBarFragment : DialogFragment() {

    private var _binding: FragmentProgressBarBinding? = null
    private val binding get() = _binding!!

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    fun updateProgress(progress : Int){
        Log.i("updateProgress", "updateProgress: " + progress)
            binding.progress = progress.toString().plus("%")
            binding.circularProgressBar.setProgressWithAnimation(progress.toFloat(),500)
            binding.executePendingBindings()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBarBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        this.isCancelable = false
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProgressBarFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            ProgressBarFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}