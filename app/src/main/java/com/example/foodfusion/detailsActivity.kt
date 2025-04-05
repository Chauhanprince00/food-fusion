package com.example.foodfusion

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.foodfusion.databinding.ActivityDetailsBinding
import com.example.foodfusion.model.cartitems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class detailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var foodname:String? = null
    private var foodimage:String? = null
    private var foodDescription:String? = null
    private var foodIngredient:String? = null
    private var foodPrice:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        foodname = intent.getStringExtra("menuItemName")
        foodDescription = intent.getStringExtra("menuItemDescription")
        foodIngredient = intent.getStringExtra("menuItemINgredient")
        foodPrice = intent.getStringExtra("menuItemprice")
        foodimage = intent.getStringExtra("menuItemURL")

        with(binding){
            detailedfoodname.text = foodname
            desscriptiontextview.text = foodDescription
            Ingredientstextview.text = foodIngredient
            Glide.with(this@detailsActivity).load(Uri.parse(foodimage)).into(detailedFoodimage)
        }


        binding.baack.setOnClickListener {
            finish()
        }
        binding.additemtocart.setOnClickListener { 
            additemtocart()
        }
    }

    private fun additemtocart() {
        val database = FirebaseDatabase.getInstance().reference
        val auth = FirebaseAuth.getInstance()
        val userid = auth.currentUser?.uid?:""
        //create a cart items object
        val cartItem = cartitems(foodname.toString(),foodPrice.toString(),foodDescription.toString(),foodimage.toString(),1)
        
        //save data to cartItem to firebase
        database.child("user").child(userid).child("CartItems").push().setValue(cartItem).addOnSuccessListener {
            Toast.makeText(this, "item added to cart", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { Toast.makeText(this, "item not added", Toast.LENGTH_SHORT).show() }
    }
}