package com.example.foodfusion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusion.databinding.ActivitySignBinding
import com.example.foodfusion.model.usermodel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var username:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSighInClient: GoogleSignInClient
    private lateinit var binding: ActivitySignBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        //initilize all variables
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        googleSighInClient = GoogleSignIn.getClient(this,googleSignInOptions)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.createaccount.setOnClickListener {
            username = binding.username.text.toString()
            email = binding.emailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (username.isBlank()||email.isBlank()||password.isBlank()){
                Toast.makeText(this, "Please all the Details", Toast.LENGTH_SHORT).show()
            }else{
                createaccount(email,password)
            }

        }
        binding.googlebutton.setOnClickListener { 
            val sighintent = googleSighInClient.signInIntent
            launcher.launch(sighintent)
        }
    }
    //launcher for google signin
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account:GoogleSignInAccount? = task.result
                val credenitial = GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credenitial).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this, "Sign In successfull", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, "Sign in failed 😓", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Toast.makeText(this, "Sign in failed 😓", Toast.LENGTH_SHORT).show()
        }
        
    }

    private fun createaccount(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
        if (task.isSuccessful){
            Toast.makeText(this, "Account create successfully", Toast.LENGTH_SHORT).show()
            saveuserdata()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }else{
            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            Log.d("Account", "createaccount: Falier",task.exception)
        }
    }
    }

    private fun saveuserdata() {
        //retrive data from inputfield
        username = binding.username.text.toString()
        password = binding.password.text.toString().trim()
        email = binding.emailAddress.text.toString().trim()

        val user  =usermodel(username,email,password)
        val userid = FirebaseAuth.getInstance().currentUser!!.uid
        //save data to firebase
        database.child("user").child(userid).setValue(user)
        
    }
}