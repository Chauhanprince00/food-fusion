package com.example.foodfusion.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfusion.databinding.PopularItemBinding
import com.example.foodfusion.detailsActivity

class PopularAdapter(private val Items:List<String>,private val price:List<String>,private val Image:List<Int>,private val requirecontext:Context):RecyclerView.Adapter<PopularAdapter.PopularViewHolder>() {
    class PopularViewHolder(private val binding: PopularItemBinding,):RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, price:String, images: Int) {
            binding.FoodNamePopular.text = item
            binding.PricePopular.text = price
            binding.imageView6.setImageResource(images)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return Items.size
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        val item = Items[position]
        val images = Image[position]
        val price = price[position]

        holder.bind(item,price,images)

        holder.itemView.setOnClickListener {
            val intent = Intent(requirecontext, detailsActivity::class.java)
            intent.putExtra("menuItemName",item)
            intent.putExtra("menuItemimage",images)
            requirecontext.startActivity(intent)
        }
    }


}