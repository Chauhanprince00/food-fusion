package com.example.foodfusion.Fragement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusion.adapter.menuAdapter
import com.example.foodfusion.databinding.FragmentSearchBinding
import com.example.foodfusion.model.menuitems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var  adapter: menuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalmenuitem = mutableListOf<menuitems>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
      //retrive menuitem from database
        retrivemenuitem()
        //setup for search view
        setupSearchView()

        return binding.root
    }

    private fun retrivemenuitem() {
        //get database ref
        database = FirebaseDatabase.getInstance()
        //ref to the menu node
        val foodRef = database.reference.child("menu")
        foodRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodsnapshot in snapshot.children){
                    val menuitem = foodsnapshot.getValue(menuitems::class.java)
                    menuitem?.let {
                        originalmenuitem.add(it)
                    }
                }
                showAllMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showAllMenu() {
        val filtermenuitem = ArrayList(originalmenuitem)
        setAdapter(filtermenuitem)
    }

    private fun setAdapter(filtermenuitem: List<menuitems>) {
        if (isAdded && context != null){
            adapter = menuAdapter(filtermenuitem,requireContext())
            binding.menurecyclerview.layoutManager = LinearLayoutManager(requireContext())
            binding.menurecyclerview.adapter = adapter
        }

    }


    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                filtermenuitems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filtermenuitems(newText)
                return true
            }
        })
    }

    private fun filtermenuitems(query: String) {
        val filterMenuItems = originalmenuitem.filter {
            it.foodfoodname?.contains(query,ignoreCase = true) == true
        }

        setAdapter(filterMenuItems)
    }
}