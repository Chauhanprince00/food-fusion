package com.example.foodfusion.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodfusion.databinding.CartitemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class cartadapter(
    private val context: Context,
    private val Cartitems: MutableList<String>,
    private val Cartitemprice: MutableList<String>,
    private var cartDescription: MutableList<String>,
    private val Cartimage: MutableList<String>,
    private val Quentity: MutableList<Int>,
    private val ingredient: MutableList<String>
) : RecyclerView.Adapter<cartadapter.cartViewHolder>() {
    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userID = auth.currentUser?.uid ?: ""
        val cartItemNumber = Cartitems.size

        itemQuentity = IntArray(cartItemNumber) { 1 }
        cartitemsRef = database.reference.child("user").child(userID).child("CartItems")


    }

    companion object {
        private var itemQuentity: IntArray = intArrayOf()
        private lateinit var cartitemsRef: DatabaseReference
    }

    inner class cartViewHolder(private val binding: CartitemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quentity = itemQuentity[position]
                cartFoodname.text = Cartitems[position]
                cartitemprice.text = "₹ ${Cartitemprice[position]}"
                //load image using glide
                val uriString = Cartimage[position]
                val Uri = Uri.parse(uriString)
                Glide.with(context).load(Uri).into(cartimage)
                cartitemquentity.text = quentity.toString()
                minusbutton.setOnClickListener {
                    decreseQuentity(position)
                }
                plusbutton.setOnClickListener {
                    increseQuentity(position)

                }
                deletebutton.setOnClickListener {
                    val itemposition = adapterPosition
                    if (itemposition != RecyclerView.NO_POSITION) (
                            deleteitem(position)
                            )
                }

            }

        }

        private fun increseQuentity(position: Int) {
            if (itemQuentity[position] < 10) {
                itemQuentity[position]++
                Quentity[position] = itemQuentity[position]
                binding.cartitemquentity.text = itemQuentity[position].toString()
            }
        }

        private fun deleteitem(position: Int) {
            getUniqueKeyAtPosition(position) { uniquekey ->
                if (uniquekey != null) {
                    removeItem(position, uniquekey)
                }
            }

        }

        private fun removeItem(position: Int, uniquekey: String) {
            if (uniquekey != null) {
                cartitemsRef.child(uniquekey).removeValue().addOnSuccessListener {

                                      notifyItemRemoved(position)
                    notifyItemRangeChanged(position,Cartitems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "failed to delete", Toast.LENGTH_SHORT).show()
                }
            } else {

            }
        }

        private fun decreseQuentity(position: Int) {
            if (itemQuentity[position] > 1) {
                itemQuentity[position]--
                Quentity[position] = itemQuentity[position]
                binding.cartitemquentity.text = itemQuentity[position].toString()
            }
        }

    }

    private fun getUniqueKeyAtPosition(positionRetrive: Int, onComplete: (String?) -> Unit) {
        cartitemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniquekey: String? = null
                //loop for snapshort children
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrive) {
                        uniquekey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                onComplete(uniquekey)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewHolder {
        val binding = CartitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return cartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return Cartitems.size
    }

    override fun onBindViewHolder(holder: cartViewHolder, position: Int) {
        holder.bind(position)
    }

    fun getUpdatedItemQuentities(): MutableList<Int> {
        val itemquantity = mutableListOf<Int>()
        itemquantity.addAll(Quentity)
        return itemquantity

    }
}