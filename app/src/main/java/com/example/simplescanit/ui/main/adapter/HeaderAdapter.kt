package com.example.simplescanit.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simplescanit.databinding.ScannedItemsHeaderLayoutBinding
import com.example.simplescanit.ui.main.model.HeaderItemModel

class HeaderAdapter : ListAdapter<HeaderItemModel, HeaderAdapter.ViewHolder>(diffUtill) {

    inner class ViewHolder(private val binding: ScannedItemsHeaderLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bindData(item: HeaderItemModel){
            binding.apply {
                itemName.text = item.itemName
                itemQuantity.text = item.quantityName
            }
        }
    }

    companion object{
        val diffUtill = object : DiffUtil.ItemCallback<HeaderItemModel>(){
            override fun areItemsTheSame(oldItem: HeaderItemModel, newItem: HeaderItemModel): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: HeaderItemModel, newItem: HeaderItemModel): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HeaderAdapter.ViewHolder {
        val binding = ScannedItemsHeaderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeaderAdapter.ViewHolder, position: Int) {
       holder.bindData(getItem(position))
    }
}