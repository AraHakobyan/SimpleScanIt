package com.example.simplescanit.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.simplescanit.databinding.ScannedItemLayoutBinding
import com.example.simplescanit.ui.main.model.DbItemModel

class ScannedItemsAdapter: ListAdapter<DbItemModel, ScannedItemsAdapter.ViewHolder>(diffUtill) {

    inner class ViewHolder(private val binding: ScannedItemLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bindData(item: DbItemModel){
            binding.apply {
                itemName.text = item.barcode
                itemQuantity.text = item.quantity?.toString()
            }
        }
    }

    companion object{
        val diffUtill = object : DiffUtil.ItemCallback<DbItemModel>(){
            override fun areItemsTheSame(oldItem: DbItemModel, newItem: DbItemModel): Boolean =
                oldItem.barcode == newItem.barcode

            override fun areContentsTheSame(oldItem: DbItemModel, newItem: DbItemModel): Boolean =
                oldItem == newItem

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScannedItemsAdapter.ViewHolder {
        val binding = ScannedItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScannedItemsAdapter.ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
}