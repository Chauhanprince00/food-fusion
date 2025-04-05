package com.example.foodfusion.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusion.databinding.RecentbuyitemBinding

class recentBuyAdapter(private var context: Context,private val foodnameList:ArrayList<String>,private val foodImageList:ArrayList<String>,private val foodPriceList:ArrayList<String>,private val foodQuantityList:ArrayList<Int>,):
    RecyclerView.Adapter<recentBuyAdapter.RecentViewHolder>() {
   inner class RecentViewHolder (private val binding: RecentbuyitemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.apply {
               FoodName.text = foodnameList[position]
               Price.text = "₹ ${foodPriceList[position]}"
               foodQuantity.text = foodQuantityList[position].toString()
                val uriString = foodImageList[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(binding.foodimage)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding = RecentbuyitemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return RecentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodnameList.size
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
       holder.bind(position)
    }

}