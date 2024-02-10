package com.irancell.nwg.ios.presentation.audit.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.response.audit.Option
import com.irancell.nwg.ios.databinding.ChildPickerItemviewBinding
import com.irancell.nwg.ios.util.findIndex


class PickerAdapter(

) :
    RecyclerView.Adapter<PickerAdapter.ViewHolder>() {
    lateinit var recyclerView : RecyclerView

    private val diffCallback: DiffUtil.ItemCallback<Option> =
        object : DiffUtil.ItemCallback<Option>() {
            override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
                return oldItem == newItem
            }
        }

    private val mDiffer: AsyncListDiffer<Option> = AsyncListDiffer(this, diffCallback)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildPickerItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), recyclerView
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mDiffer.currentList[position])
    }



    override fun getItemCount() =  mDiffer.currentList.size;
    fun submitList(list: List<Option>) {
        mDiffer.submitList(list)
    }

    class ViewHolder constructor(
        private val binding: ChildPickerItemviewBinding,
        private val recyclerView: RecyclerView
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Option) {
            binding.option = item
            binding.executePendingBindings()

        }
    }
}