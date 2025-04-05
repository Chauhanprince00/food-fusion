package com.example.foodfusion.Fragement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodfusion.R
import com.example.foodfusion.databinding.FragmentProfileBinding
import com.example.foodfusion.model.usermodel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        setUserData()
        
        binding.saveinfo.setOnClickListener { 
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val address = binding.address.text.toString()
            val phone = binding.phone.text.toString()
            updateUserData(name,email,address,phone)
        }
        binding.apply {
            name.isEnabled = false
            email.isEnabled = false
            address.isEnabled = false
            phone.isEnabled= false
        }
        binding.editbutton.setOnClickListener {
            binding.apply {

                name.isEnabled = !name.isEnabled
                email.isEnabled= !email.isEnabled
                address.isEnabled= !address.isEnabled
                phone.isEnabled= !phone.isEnabled
            }
        }
        return binding.root
    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid
        if (userId != null){
            val userRef = database.getReference("user").child(userId)
            val userdata = hashMapOf(
                "name" to name,
                "email" to email,
                "address" to address,
                "phone" to phone)
            userRef.setValue(userdata).addOnSuccessListener {
                Toast.makeText(requireContext(), "profile update successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null){
            val userRef = database.getReference("user").child(userId)

            userRef.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userProfile = snapshot.getValue(usermodel::class.java)
                        if (userProfile != null){
                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.phone.setText(userProfile.phone)
                            binding.email.setText(userProfile.email)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
        }
    }


}