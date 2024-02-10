package com.irancell.nwg.ios.presentation.audit.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.flexbox.*
import com.irancell.nwg.ios.data.remote.response.audit.AttrElement
import com.irancell.nwg.ios.data.remote.response.audit.SubGroup
import com.irancell.nwg.ios.databinding.SubGroupGeneratedItemviewBinding
import com.irancell.nwg.ios.databinding.SubGroupItemviewBinding
import com.irancell.nwg.ios.util.SwitchGenerator
import com.irancell.nwg.ios.util.collapse
import com.irancell.nwg.ios.util.expand


class SubGroupAdapter(
    private val lifecycleCoroutineScope: LifecycleCoroutineScope,
    private val clickListener: SubGroupListener,
    private val local: String,

    ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var subGroups: ArrayList<SubGroup> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return ViewHolder(
            lifecycleCoroutineScope,
            SubGroupItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), subGroups, local, clickListener
        )


    }

    override fun getItemCount(): Int {
        return subGroups.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = subGroups[position]
        (holder as ViewHolder).bind(item)


    }


    class ViewHolder constructor(
        val lifecycleCoroutineScope: LifecycleCoroutineScope,
        val binding: SubGroupItemviewBinding,
        val subGroups: ArrayList<SubGroup>,
        val local: String,
        private val clickListener: SubGroupListener
    ) : RecyclerView.ViewHolder(binding.root),
        AttributeListener {

        var adapter: AttrElementAdapter

        init {

            binding.RVElements.apply {
                layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                (binding.RVElements.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false
            }

            adapter = AttrElementAdapter(lifecycleCoroutineScope, this, local)

        }


        fun bind(item: SubGroup) {
            binding.data = item
            binding.local = local
            binding.executePendingBindings()
            item.element?.let {
                adapter.elements = it
                binding.RVElements.adapter = adapter
                if (subGroups.size > 0)
                    adapter.panoramicElements = subGroups[0].element!!
            }
        }

        override fun onEvent(
            attrElement: AttrElement,
            innerGroupPosition: Int?,
            position: Int,
            action: Int
        ) {
            if (action == SwitchGenerator) {
                binding.RVElements.scrollToPosition(position)
            }
            clickListener.onEvent(
                adapterPosition,
                position,
                innerGroupPosition,
                action
            )
        }

    }
}


interface SubGroupListener {
    fun onEvent(
        subGroupPosition: Int,
        attrElementPosition: Int,
        innerGroupPosition: Int?,
        action: Int
    )
}

