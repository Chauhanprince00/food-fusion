package com.example.foodfusion.Fragement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodfusion.R
import com.example.foodfusion.adapter.BuyAgainAdapter
import com.example.foodfusion.databinding.FragmentHistoryBinding
import com.example.foodfusion.model.OrderDetails
import com.example.foodfusion.recentorderitems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyagainadapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        //retrive and display the user order History
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retriveBuyHistry()

        binding.receivedbutton.setOnClickListener {
            updateOrderStatus()
        }

        binding.recentitem.setOnClickListener {
            seeItemRecentBuy()
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val completeOrderRef = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderRef.child("paymentReceived").setValue(true)
    }

    private fun seeItemRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentbuy ->
            val intent = Intent(requireContext(), recentorderitems::class.java)
            intent.putExtra("recentBuyOrderITem", ArrayList(listOfOrderItem))
            startActivity(intent)
        }
    }

    private fun retriveBuyHistry() {
        binding.recentbuyitem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""
        val buyItemRef: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortQurey = buyItemRef.orderByChild("currentTime")
        shortQurey.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItem.clear()
                for (buySnapshort in snapshot.children) {
                    val buyHistryitem = buySnapshort.getValue(OrderDetails::class.java)
                    buyHistryitem?.let {
                        listOfOrderItem.add(it)
                    }
                }
                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    setDataInRecentBuyItem()
                    setPreviousBuyItemRecyclerView()
                    setDataInRecentItembuy()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setDataInRecentBuyItem() {
        if (!isAdded || context == null) return  // 🚀 Check if fragment is attached

        binding.recentbuyitem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItem.firstOrNull()

        recentOrderItem?.let {
            binding.buyagainfoodname.text = it.foodNames?.firstOrNull() ?: ""
            binding.buyagainprice.text = "₹" + (it.foodPrices?.firstOrNull() ?: "")
            val image = it.foodImages?.firstOrNull() ?: ""

            val uri = Uri.parse(image)
            Glide.with(requireContext()).load(uri).into(binding.buyagainfoodimage)

            val isOrderAccepted = it.orderAccepted
            val isPaymentReceived = it.dispatched
            val isDelivered = it.Delivered
            val name = it.userName
            val amount = it.totalPrice
            val foodname = it.foodNames
            Log.d(
                "TAG",
                "setDataInRecentBuyItem: OrderAccepted = $isOrderAccepted, PaymentReceived = $isPaymentReceived"
            )

            when {
                !isDelivered && !isPaymentReceived && !isOrderAccepted -> {
                    binding.orderstatustext.text =
                        "\uD83D\uDED2 Order Placed! Waiting for vendor approval. ✅ Your order will be accepted soon! \uD83D\uDE80"
                    binding.orderstatus.setAnimation(R.raw.waitiing)
                    binding.orderstatus.playAnimation()
                }

                isOrderAccepted && isPaymentReceived && !isDelivered -> {
                    binding.orderstatustext.text =
                        "\uD83D\uDE9A Order On The Way! Your package is en route! \uD83D\uDCE6\n" +
                                "\uD83D\uDCE2 Once delivered, please tap \"Received\" to confirm. ✅"
                    binding.orderstatus.setAnimation(R.raw.orderdispatched)
                    binding.orderstatus.playAnimation()
                    binding.receivedbutton.visibility = View.VISIBLE
                    binding.receivedbutton.setOnClickListener {
                        updateOrderdeliveryStatus()
                        setNotificationIntodatabase(name,foodname,amount)

                    }
                }

                isOrderAccepted && !isPaymentReceived && !isDelivered -> {
                    binding.orderstatustext.text =
                        "✅ Order Accepted! Your order is being prepared for dispatch. \uD83D\uDE80"
                    binding.orderstatus.setAnimation(R.raw.prepareorder)
                    binding.orderstatus.playAnimation()
                }

                else -> {
                    binding.orderstatustext.text =
                        "✅ Order Delivered! Your order has been successfully completed. \uD83C\uDF89"
                    binding.orderstatus.setAnimation(R.raw.orderplaced)
                    binding.orderstatus.playAnimation()
                    binding.receivedbutton.visibility = View.GONE
                }
            }
        }
    }

    private fun setNotificationIntodatabase(
        name: String?,
        foodname: MutableList<String>?,
        amount: String?
    ) {
        val userId = auth.currentUser?.uid
        val Message = "Dear $name,\n your order for $foodname has been delivered ✅. The total amount paid is ₹$amount.\n" +
                "\n" +
                "We hope you enjoy your meal! \uD83C\uDF7D\uFE0F\uD83D\uDE0B Thank you for choosing us! \uD83E\uDD1D✨"
        val database = FirebaseDatabase.getInstance().reference
        val databaseRef = database.child("user").child(userId!!).child("notification")
        //date
        val currentTime = System.currentTimeMillis()
        val dateFormate = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val formatedDate = dateFormate.format(Date(currentTime))
        //formate time
        val timeFormate = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formatedTime = timeFormate.format(Date(currentTime))

        //unique key for notification
        val notificationUniquekey = databaseRef.push().key?: return

        //notification Object
        val notification = mapOf(
            "Heading" to "✅ Order Delivered! \uD83C\uDF89",
            "message" to Message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false,
            "time" to formatedTime,
            "date" to formatedDate
        )
        //save notification to firebase
        databaseRef.child(notificationUniquekey).setValue(notification)
    }


    private fun updateOrderdeliveryStatus() {
        val itemPushKey = listOfOrderItem[0].itemPushKey
        val buyHistryOrderRef =
            database.reference.child("user").child(userId).child("BuyHistory").child(itemPushKey!!)
        buyHistryOrderRef.child("delivered").setValue(true)

        val completeOrderRef = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderRef.child("delivered").setValue(true)
    }


    private fun setPreviousBuyItemRecyclerView() {
        val buyagainFoodname = mutableListOf<String>()
        val buyagainitemprice = mutableListOf<String>()
        val buyagainimage = mutableListOf<String>()

        for (i in 1 until listOfOrderItem.size) {
            listOfOrderItem[i].foodNames?.firstOrNull()?.let { buyagainFoodname.add(it) }
            listOfOrderItem[i].foodPrices?.firstOrNull()?.let { buyagainitemprice.add(it) }
            listOfOrderItem[i].foodImages?.firstOrNull()?.let { buyagainimage.add(it) }
        }
        val rv = binding.buyagainrecyclerview
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyagainadapter =
            BuyAgainAdapter(buyagainFoodname, buyagainitemprice, buyagainimage, requireContext())
        rv.adapter = buyagainadapter
    }

    private fun setDataInRecentItembuy() {

    }

}