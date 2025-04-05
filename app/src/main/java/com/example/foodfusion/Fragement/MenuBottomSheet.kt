package com.example.foodfusion.Fragement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusion.adapter.menuAdapter
import com.example.foodfusion.databinding.FragmentMenuBottomSheetBinding
import com.example.foodfusion.model.menuitems
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MenuBottomSheet :BottomSheetDialogFragment() {
    private lateinit var binding:FragmentMenuBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems : MutableList<menuitems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBottomSheetBinding.inflate(layoutInflater)

       retrivemenuitems()

        binding.backbutton.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun retrivemenuitems() {
        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("menu")
        menuItems = mutableListOf()

        foodRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodsnapshot in snapshot.children){
                    val menuItem = foodsnapshot.getValue(menuitems::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                Log.d("items", "onDataChange: data received")
                // once data received , set to adapter
                setadapter()
            }



            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun setadapter() {
        if (menuItems.isNotEmpty()){
            val adapter = menuAdapter(menuItems,requireContext())
            binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.menuRecyclerView.adapter = adapter
            Log.d("items", "setadapter: data set")
        }else{
            Log.d("items", "setadapter: data not set")
        }

    }


}