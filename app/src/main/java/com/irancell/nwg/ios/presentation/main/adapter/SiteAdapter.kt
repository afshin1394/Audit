package com.irancell.nwg.ios.presentation.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.irancell.nwg.ios.R
import com.irancell.nwg.ios.data.model.SiteDomain
import com.irancell.nwg.ios.databinding.SiteItemviewBinding
import com.irancell.nwg.ios.util.collapse
import com.irancell.nwg.ios.util.constants.Audit
import com.irancell.nwg.ios.util.constants.Problematic
import com.irancell.nwg.ios.util.constants.Routing
import com.irancell.nwg.ios.util.expand
import com.irancell.nwg.ios.util.setAnimationClick


class SiteAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val clickListener: SiteClickListener
) : RecyclerView.Adapter<SiteAdapter.ViewHolder>() {
    lateinit var language: String
    override fun getItemCount() =  mDiffer.currentList.size;
        private val diffCallback: DiffUtil.ItemCallback<SiteDomain> =
            object : DiffUtil.ItemCallback<SiteDomain>() {
                override fun areItemsTheSame(oldItem: SiteDomain, newItem: SiteDomain): Boolean {
                    return oldItem.siteId == newItem.siteId
                }

                override fun areContentsTheSame(oldItem: SiteDomain, newItem: SiteDomain): Boolean {
                    return oldItem == newItem
                }
            }

    private val mDiffer: AsyncListDiffer<SiteDomain> = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<SiteDomain>) {
        mDiffer.submitList(list)
    }
    class ViewHolder constructor(
        private val lifecycleCoroutineScope: LifecycleCoroutineScope,
        private val binding: SiteItemviewBinding,
        private val clickListener: SiteClickListener,
        private val language: String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SiteDomain) {
            binding.data = item
            binding.local = language
            binding.executePendingBindings()


            binding.frmSite.setOnClickListener {
                if (item.isOpen) {
                    item.isOpen = false
                    collapse(binding.linExpandable)
                    binding.ivExpand.rotation = 0f
                } else {
                    item.isOpen = true
                    expand(binding.linExpandable)
                    binding.ivExpand.rotation = 180f

                }
            }

            binding.btnAudit.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onSiteClick(adapterPosition, item, Audit)
            }
            binding.btnProblematic.setAnimationClick(lifecycleCoroutineScope, R.anim.button_click) {
                clickListener.onSiteClick(adapterPosition, item, Problematic)
            }
            binding.btnRouting.setAnimationClick(lifecycleCoroutineScope,R.anim.button_click){
                clickListener.onSiteClick(adapterPosition, item, Routing)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            lifecycleCoroutineScope,
            SiteItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), clickListener, language
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mDiffer.currentList[position]
        holder.bind(item)
    }

}

interface SiteClickListener {
    fun onSiteClick(position: Int, site: SiteDomain, action: Int)
}
