package com.irancell.nwg.ios.presentation.audit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.remote.response.audit.Option
import com.irancell.nwg.ios.databinding.AttrOptionItemviewBinding
import com.irancell.nwg.ios.util.setAnimationDelayedClick

class OptionsAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val clickListener : OptionListener
) : RecyclerView.Adapter<OptionsAdapter.ViewHolder>(){
      lateinit var  options : ArrayList<Option>
     class ViewHolder  constructor(private val lifecycleCoroutineScope: LifecycleCoroutineScope,private val binding: AttrOptionItemviewBinding,private val clickListener: OptionListener,private val options: ArrayList<Option>) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Option) {
          binding.data = item
          binding.executePendingBindings()
                binding.checkboxChoice.setAnimationDelayedClick(lifecycleCoroutineScope,R.anim.button_click,10){
                    clickListener.onOptionClick(item,adapterPosition, options)
                }
            }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(lifecycleCoroutineScope,
            AttrOptionItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), clickListener,options
        )
    }

    override fun getItemCount(): Int {
       return options.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItemAt(position))
    }
    private fun getItemAt(position: Int): Option {
        return options[position]
    }

}
interface OptionListener {
    fun onOptionClick(selectedOption : Option, position: Int, options: ArrayList<Option>)
}