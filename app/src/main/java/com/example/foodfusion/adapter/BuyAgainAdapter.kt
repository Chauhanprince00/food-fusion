package com.example.foodfusion.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusion.databinding.BuyAgainItemBinding

class BuyAgainAdapter(private val buyagainfoodname:MutableList<String>,private val buyAgainFoodPrice:MutableList<String>,private val buyagainimage:MutableList<String>,private val requirecontext:Context):RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {
   inner class BuyAgainViewHolder(private val binding: BuyAgainItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(foodname: String, foodprice: String, foodimage: String) {
            binding.buyagainfoodname.text = foodname
            binding.buyagainprice.text = foodprice
            val uriString = foodimage
            val uri = Uri.parse(uriString)
            Glide.with(requirecontext).load(uri).into(binding.buyagainfoodimage)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
      val binding  = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount(): Int =buyagainfoodname.size

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyagainfoodname[position],buyAgainFoodPrice[position],buyagainimage[position])
    }
}