package com.example.foodfusion.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusion.databinding.MenuItemBinding
import com.example.foodfusion.detailsActivity
import com.example.foodfusion.model.menuitems
import java.net.URI

class menuAdapter(private val menuitems: List<menuitems>, private val context: Context) :
    RecyclerView.Adapter<menuAdapter.MenuViewHolder>() {
    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    opendetailActivity()

                }
            }
        }

        private fun opendetailActivity() {
            val menuitem = menuitems[position]

            val intent = Intent(context, detailsActivity::class.java).apply {
                putExtra("menuItemName", menuitem.foodfoodname)
                putExtra("menuItemprice", menuitem.foodprice)
                putExtra("menuItemURL", menuitem.foodimage)
                putExtra("menuItemDescription", menuitem.fooddescription)
                putExtra("menuItemINgredient", menuitem.foodingredient)

            }
            context.startActivity(intent)
        }
        //set data into recuclerview items name,price,image
        fun bind(position: Int) {
            val menuitemes = menuitems[position]
            binding.apply {

                menuFoodname.text = menuitemes.foodfoodname
                menuprice.text = "₹ ${menuitemes.foodprice}"
                val uri = Uri.parse(menuitemes.foodimage)
                Glide.with(context).load(uri).into(menuimage)


            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuitems.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

}


