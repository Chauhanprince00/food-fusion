package com.example.foodfusion

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusion.adapter.recentBuyAdapter
import com.example.foodfusion.databinding.ActivityRecentorderitemsBinding
import com.example.foodfusion.model.OrderDetails

class recentorderitems : AppCompatActivity() {
    private lateinit var binding: ActivityRecentorderitemsBinding
    private lateinit var allfoodname : ArrayList<String>
    private lateinit var allfoodimage : ArrayList<String>
    private lateinit var allfoodPrice : ArrayList<String>
    private lateinit var allfoodQuantity : ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecentorderitemsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backbutton.setOnClickListener {
            finish()
        }
        val recentOrderItems = intent.getSerializableExtra("recentBuyOrderITem") as ArrayList<OrderDetails>
        recentOrderItems?.let { orderdetails->
            if (orderdetails.isNotEmpty()){
                val recentOrderItem = orderdetails[0]
                allfoodname = recentOrderItem.foodNames as ArrayList<String>
                allfoodimage = recentOrderItem.foodImages as ArrayList<String>
                allfoodPrice = recentOrderItem.foodPrices as ArrayList<String>
                allfoodQuantity = recentOrderItem.foodQuentities as ArrayList<Int>
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.recyclerview
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = recentBuyAdapter(this,allfoodname,allfoodimage,allfoodPrice,allfoodQuantity)
        rv.adapter = adapter
    }
}