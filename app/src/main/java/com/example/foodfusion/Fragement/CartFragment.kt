package com.example.foodfusion.Fragement

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusion.MainActivity
import com.example.foodfusion.R
import com.example.foodfusion.adapter.cartadapter
import com.example.foodfusion.databinding.FragmentCartBinding
import com.example.foodfusion.model.cartitems
import com.example.foodfusion.payoutActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodName: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDescription: MutableList<String>
    private lateinit var foodImageUri: MutableList<String>
    private lateinit var foodIngredients: MutableList<String>
    private lateinit var quentity: MutableList<Int>
    private lateinit var cartadapter: cartadapter
    private lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        //inilization
        auth = FirebaseAuth.getInstance()
        retriveCartItems()





        binding.proceedbutton.setOnClickListener {
            // get ordered item details before processing to chekout
            getOrderItemDetails()
        }

        return binding.root
    }

    private fun getOrderItemDetails() {

        val orderiIdRederence = database.reference.child("user").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDescription = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()
        //get items quentitys
        val foodQuentities = cartadapter.getUpdatedItemQuentities()
        orderiIdRederence.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the cart items to respective list
                    val orderitems = foodSnapshot.getValue(cartitems::class.java)
                    //add items details into list
                    orderitems?.foodName?.let { foodName.add(it) }
                    orderitems?.foodPrice?.let { foodPrice.add(it) }
                    orderitems?.foodDescription?.let { foodDescription.add(it) }
                    orderitems?.foodImage?.let { foodImage.add(it) }
                    orderitems?.foodIngredint?.let { foodIngredient.add(it) }
                }
                ordernow(
                    foodName,
                    foodPrice,
                    foodDescription,
                    foodImage,
                    foodIngredient,
                    foodQuentities
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "order making failed , please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun ordernow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDescription: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQuentities: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), payoutActivity::class.java)
            intent.putExtra("foodItemName", foodName as ArrayList<String>)
            intent.putExtra("foodItemprice", foodPrice as ArrayList<String>)
            intent.putExtra("foodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("foodItemDescription", foodDescription as ArrayList<String>)
            intent.putExtra("foodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("quentity", foodQuentities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retriveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodRef: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")
        //list to store cart items
        foodName = mutableListOf()
        foodPrices = mutableListOf()
        foodDescription = mutableListOf()
        foodImageUri = mutableListOf()
        foodIngredients = mutableListOf()
        quentity = mutableListOf()
        //fetch data from database
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodName.clear()
                foodPrices.clear()
                foodDescription.clear()
                foodImageUri.clear()
                foodIngredients.clear()
                foodName.clear()
                if (!isAdded || activity == null) return
                for (foofSnapshot in snapshot.children) {
                    val cartItems = foofSnapshot.getValue(cartitems::class.java)
                    //add cart items details to the list
                    cartItems?.foodName?.let { foodName.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDescription.add(it) }
                    cartItems?.foodImage?.let { foodImageUri.add(it) }
                    cartItems?.foodQuantity?.let { quentity.add(it) }
                    cartItems?.foodIngredint?.let { foodIngredients.add(it) }
                }
                setAdapter()

            }

            private fun setAdapter() {
                    cartadapter = cartadapter(
                        requireContext(),
                        foodName,
                        foodPrices,
                        foodDescription,
                        foodImageUri,
                        quentity,
                        foodIngredients
                    )
                    binding.cartrecyclerview.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                    binding.cartrecyclerview.adapter = cartadapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not fetch", Toast.LENGTH_SHORT).show()
            }

        })
    }


}