package com.irancell.nwg.ios.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.irancell.nwg.ios.data.model.ProjectDomain
import com.irancell.nwg.ios.databinding.RegionViewItemviewBinding

class ProjectAdapter (private val clickListener: RegionListener) :   ListAdapter<ProjectDomain, ProjectAdapter.ViewHolder>(
    RegionDiffCallback()
) {

    lateinit var language : String

    class RegionDiffCallback : DiffUtil.ItemCallback<ProjectDomain>() {

        override fun areItemsTheSame(oldItem: ProjectDomain, newItem: ProjectDomain): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProjectDomain, newItem: ProjectDomain): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class ViewHolder constructor( private val binding: RegionViewItemviewBinding,private val clickListener: RegionListener,private val language : String) :
        RecyclerView.ViewHolder(binding.root){

        fun bind(item: ProjectDomain) {
            binding.data = item
            binding.local = language
            binding.executePendingBindings()
            binding.root.setOnClickListener{
                clickListener.requestSite(adapterPosition,item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RegionViewItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),clickListener,language)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


}

interface RegionListener {
 fun requestSite(position: Int, projectDomain: ProjectDomain)
}
