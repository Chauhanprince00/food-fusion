package com.example.foodfusion.Fragement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodfusion.adapter.notificationAdapter
import com.example.foodfusion.databinding.FragmentNotificationBottomFragementBinding
import com.example.foodfusion.model.notificationModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class notificationBottom_Fragement : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentNotificationBottomFragementBinding
    private lateinit var notificationAdapter: notificationAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var notificationlist = mutableListOf<notificationModel>()
    private var valueEventListener: ValueEventListener? = null
    private lateinit var notificationRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBottomFragementBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        notificationRef = database.child("user").child(auth.currentUser!!.uid).child("notification")

        fetchNotifications()

        notificationAdapter = notificationAdapter(notificationlist)
        binding.notificationrecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationrecyclerview.adapter = notificationAdapter

        return binding.root
    }

    private fun fetchNotifications() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationlist.clear()
                for (notificationsnap in snapshot.children) {
                    val notification = notificationsnap.getValue(notificationModel::class.java)
                    notification?.let { notificationlist.add(it) }
                }
                notificationlist.reverse()
                notificationAdapter.notifyDataSetChanged()
                markNotificationAsRead()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        }
        notificationRef.addValueEventListener(valueEventListener!!)
    }

    private fun markNotificationAsRead() {
        notificationRef.get().addOnSuccessListener { snapshot ->
            for (notificationsnap in snapshot.children) {
                notificationsnap.ref.child("isRead").setValue(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        valueEventListener?.let { notificationRef.removeEventListener(it) }
    }
}
