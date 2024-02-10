package com.irancell.nwg.ios.presentation.audit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irancell.nwg.ios.data.remote.response.audit.Group
import com.irancell.nwg.ios.databinding.InnerGroupItemviewBinding

class InnerGroupAdapter(var groups: ArrayList<Group>?, val local: String, private val clickListener :InnerGroupListener
) : RecyclerView.Adapter<InnerGroupAdapter.ViewHolderInnerGroup>()  {
    class ViewHolderInnerGroup constructor(private val binding: InnerGroupItemviewBinding,private val local : String,private val clickListener: InnerGroupListener) : RecyclerView.ViewHolder(binding.root)  {

        fun bind(item: Group) {
          binding.data = item
          binding.local = local
          binding.executePendingBindings()
            binding.root.setOnClickListener {
                clickListener.onClick(item, position = adapterPosition)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderInnerGroup {
        return ViewHolderInnerGroup(
            InnerGroupItemviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),local,clickListener )

    }

    override fun onBindViewHolder(holder: ViewHolderInnerGroup, position: Int) {
        holder.bind(getItemAt(position))
    }

    override fun getItemCount(): Int {
       return groups!!.size
    }
    private fun getItemAt(position: Int): Group {
        return groups!![position]
    }


}

interface InnerGroupListener{
    fun onClick(innerGroup: Group, position: Int)
}
