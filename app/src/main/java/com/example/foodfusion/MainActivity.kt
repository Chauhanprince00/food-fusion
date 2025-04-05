package com.example.foodfusion

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodfusion.Fragement.notificationBottom_Fragement
import com.example.foodfusion.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database:DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var unreadCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        checkNewNotifications()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
            var NavController = findNavController(R.id.fragmentContainerView2)
            var bottomnav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomnav.setupWithNavController(NavController)

            binding.notificationbutton.setOnClickListener {
                val bottomsheetdailog = notificationBottom_Fragement()
                bottomsheetdailog.show(supportFragmentManager,"Test")
            }
    }

    private fun checkNewNotifications() {
        val userId = auth.currentUser?.uid
        val notificationRef = database.child("user").child(userId!!).child("notification")

        notificationRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                unreadCount = 0
                for (notificationsnap in snapshot.children){
                    val isRead = notificationsnap.child("isRead").getValue(Boolean::class.java)
                    if (!isRead!!){
                        unreadCount++
                    }
                }
                updateNotificationBadge()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateNotificationBadge() {
        if (unreadCount > 0){
            binding.count.visibility = View.VISIBLE
            binding.count.text = unreadCount.toString()
        }else{
            binding.count.visibility = View.GONE
        }
    }
}