package com.example.foodfusion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusion.databinding.ActivityPayoutBinding
import com.example.foodfusion.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class payoutActivity : AppCompatActivity(),PaymentResultListener {
    private lateinit var binding: ActivityPayoutBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var name:String
    private lateinit var address:String
    private lateinit var phone:String
    private lateinit var totalamount:String
    private lateinit var foodItemName:ArrayList<String>
    private lateinit var foodItemprice:ArrayList<String>
    private lateinit var foodItemImage:ArrayList<String>
    private lateinit var foodItemDescription:ArrayList<String>
    private lateinit var foodItemIngredient:ArrayList<String>
    private lateinit var Quentity:ArrayList<Int>
    private lateinit var userID:String
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //inilization
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Checkout.preload(this@payoutActivity)
            //set user data
        setUserData()

        //get user details from firebase
        val intent = intent
        foodItemName = intent.getStringArrayListExtra("foodItemName") as ArrayList<String>
        foodItemprice = intent.getStringArrayListExtra("foodItemprice") as ArrayList<String>
        foodItemImage = intent.getStringArrayListExtra("foodItemImage") as ArrayList<String>
        foodItemDescription = intent.getStringArrayListExtra("foodItemDescription") as ArrayList<String>
        foodItemIngredient = intent.getStringArrayListExtra("foodItemIngredient") as ArrayList<String>
        Quentity = intent.getIntegerArrayListExtra("quentity") as ArrayList<Int>

        totalamount = calculateTotalAmount().toString()

        binding.totalamount.isEnabled = false
        binding.totalamount.setText("₹ $totalamount")
        binding.placemyorder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            if (name.isBlank()||name.isEmpty()&&address.isBlank()||address.isEmpty()&&phone.isBlank()||phone.isEmpty()){
                Toast.makeText(this, "please fill all details from profile section", Toast.LENGTH_SHORT).show()
            }else{
                startpayment(name,address,phone)
            }


        }
        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun calculateTotalAmount(): Int {
        var totalamount = 0
        for (i in 0 until foodItemprice.size){
            val price = foodItemprice[i].toInt()
            var quentity:Int = Quentity[i]
            totalamount += price *quentity
        }
        return totalamount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null){
            val userID = user.uid
            val userRef = databaseReference.child("user").child(userID)
            userRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val names = snapshot.child("name").getValue(String::class.java)?:""
                        val addresss = snapshot.child("address").getValue(String::class.java)?:""
                        val phones = snapshot.child("phone").getValue(String::class.java)?:""
                        binding.apply {
                            name.setText(names)
                            address.setText(addresss)
                            phone.setText(phones)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

    }

    private fun startpayment(name: String, address: String, phone: String) {
        val chekout = Checkout()
        chekout.setKeyID("rzp_test_s4HzNb2gJijOwP")

        try {
            val options = JSONObject()
            //convert amount in paisa for rasorpay
            var rasorpayamount = totalamount.toInt()*100

            // store data in SharedPrefrence
            val sharepreference = getSharedPreferences("paymentData", MODE_PRIVATE)
            val editor = sharepreference.edit()
            editor.putString("name", this.name)
            editor.putString("address", this.address)
            editor.putString("phone", this.phone)
            editor.apply()

            options.put("name", this.name)
            options.put("description","Demo")
            options.put("theme.color", "#1FC57A");
            options.put("currency","INR");
            options.put("amount",rasorpayamount)
            options.put("image", "https://sdmntprwestus.oaiusercontent.com/files/00000000-c038-5230-a68b-2cdadd1781f2/raw?se=2025-04-03T08%3A25%3A28Z&sp=r&sv=2024-08-04&sr=b&scid=04748a87-0088-54ab-814f-f5681526b27d&skoid=72d71449-cf2f-4f10-a498-f160460104ee&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-04-02T16%3A08%3A33Z&ske=2025-04-03T16%3A08%3A33Z&sks=b&skv=2024-08-04&sig=qop%2BGfq6dppopEYsHgL5UrXccvD/AswzUhH2K2FzdGk%3D")

            val prefill = JSONObject()
            prefill.put("email","princeChauhan@gmail.com")
            prefill.put("contact", this.phone)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            chekout.open(this@payoutActivity,options)
        }catch (e:Exception){
            Toast.makeText(this, "Error in Payment", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        userID = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itempushkey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(userID,name,foodItemName,foodItemprice,foodItemImage,Quentity,address,totalamount,phone,time,itempushkey,false,false,false)
        val orderRef = databaseReference.child("OrderDetails").child(itempushkey!!)
        orderRef.setValue(orderDetails).addOnSuccessListener {
            removeItemFromCart()
            addOrderToHistory(orderDetails)
            addNotificationInUserNode()
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }

    private fun addNotificationInUserNode() {
        val userId = auth.currentUser?.uid
        val Message = "Dear $name,\n" +
                "Your order for $foodItemName has been placed successfully! ✅ The total amount is ₹$totalamount.\n" +
                "\n" +
                "⏳ Please wait while the vendor reviews and confirms your order. You will be notified once it's accepted.\n" +
                "\n" +
                "Thank you for choosing us! \uD83C\uDF7D\uFE0F✨ We appreciate your trust. \uD83D\uDE0A"
        val database = FirebaseDatabase.getInstance().reference
        val databaseRef = database.child("user").child(userId!!).child("notification")
        //date
        val currentTime = System.currentTimeMillis()
        val dateFormate = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val formatedDate = dateFormate.format(Date(currentTime))
        //formate time
        val timeFormate = SimpleDateFormat("hh:mm a",Locale.getDefault())
        val formatedTime = timeFormate.format(Date(currentTime))

        //unique key for notification
        val notificationUniquekey = databaseRef.push().key?: return

        //notification Object
        val notification = mapOf(
            "Heading" to "Order Placed 🎉",
            "message" to Message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
           "time" to formatedTime,
            "date" to formatedDate
        )
        //save notification to firebase
        databaseRef.child(notificationUniquekey).setValue(notification)
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("user").child(userID).child("BuyHistory")
            .child(orderDetails.itemPushKey!!)
            .setValue(orderDetails).addOnSuccessListener {

            }
    }

    private fun removeItemFromCart() {
        val cartItemRef = databaseReference.child("user").child(userID).child("CartItems")
        cartItemRef.removeValue()
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "failed to order 😔", Toast.LENGTH_SHORT).show()
    }
}