package com.example.foodfusion.Fragement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodfusion.R
import com.example.foodfusion.adapter.PopularAdapter
import com.example.foodfusion.adapter.menuAdapter
import com.example.foodfusion.databinding.FragmentHomeBinding
import com.example.foodfusion.model.menuitems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var Menuitems: MutableList<menuitems>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        binding.ViewAllMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheet()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }
        //retrive and display popular item
        retriveAndDisplayPopularItem()
        return binding.root


    }

    private fun retriveAndDisplayPopularItem() {
        //get reference to the database
        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child("menu")
        Menuitems = mutableListOf()

        //retrive menu items from the database
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Menuitems.clear()
                for (foodsnepshot in snapshot.children) {
                    val menuitem = foodsnepshot.getValue(menuitems::class.java)
                    menuitem?.let { Menuitems.add(it) }
                }
                //display random display items
                randompopularitems()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun randompopularitems() {
        //create a shuffled list of menu item
        val index = Menuitems.indices.toList().shuffled()
        val itemtoshow = 6
        val subsetMenuItem = index.take(itemtoshow).map { Menuitems[it] }
        setPopularItemAdapter(subsetMenuItem)
    }

    private fun setPopularItemAdapter(subsetMenuItem: List<menuitems>) {
        val adapter = menuAdapter(subsetMenuItem, requireContext())
        binding.populareecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.populareecyclerview.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val imagelist = ArrayList<SlideModel>()
        imagelist.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imagelist.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageslider = binding.imageSlider
        imageslider.setImageList(imagelist)
        imageslider.setImageList(imagelist, ScaleTypes.FIT)

        imageslider.setItemClickListener(object : ItemClickListener {
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imagelist[position]
                val itemMessage = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}