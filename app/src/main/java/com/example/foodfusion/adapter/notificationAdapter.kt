package com.example.foodfusion.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodfusion.R
import com.example.foodfusion.databinding.NotificationitemBinding
import com.example.foodfusion.model.notificationModel
import kotlinx.coroutines.NonDisposableHandle.parent

class notificationAdapter(private val notificationlist:List<notificationModel>):RecyclerView.Adapter<notificationAdapter.notificationviewholder>() {
    inner class notificationviewholder(private val binding: NotificationitemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.notificationtextview.text =notificationlist[position].message
            binding.heading.text = notificationlist[position].Heading
            Log.d("TAG", "bind: ${notificationlist[position].Heading}")
            binding.time.text = notificationlist[position].time
            val formattedDate = notificationlist[position].date.chunked(2).joinToString("/")
            binding.date.text = formattedDate


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notificationviewholder {
        val binding = NotificationitemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return notificationviewholder(binding)
    }

    override fun getItemCount(): Int  = notificationlist.size

    override fun onBindViewHolder(holder: notificationviewholder, position: Int) {
        holder.bind(position)
    }
}