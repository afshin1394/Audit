package com.irancell.nwg.ios.presentation.dialog.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.data.remote.response.audit.Name
import com.irancell.nwg.ios.databinding.FragmentDialogListBinding
import com.irancell.nwg.ios.databinding.ListDialogItemviewBinding
import com.irancell.nwg.ios.databinding.SiteItemviewBinding
import com.irancell.nwg.ios.presentation.main.adapter.SiteAdapter
import com.irancell.nwg.ios.presentation.main.adapter.SiteClickListener

class ListDialogAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
) : ListAdapter<Name, ListDialogAdapter.ViewHolder>(
    SiteDiffCallback()
) {
    lateinit var language: String
    override fun getItemCount() = currentList.size
    class SiteDiffCallback : DiffUtil.ItemCallback<Name>() {

        override fun areItemsTheSame(oldItem: Name, newItem: Name): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Name, newItem: Name): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: ListDialogItemviewBinding,
        private val language: String
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Name) {
            binding.data = item
            binding.language = language
            binding.executePendingBindings()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            lifecycleCoroutineScope,
            ListDialogItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), language
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


}


