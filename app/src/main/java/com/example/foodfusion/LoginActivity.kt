package com.example.foodfusion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodfusion.databinding.ActivityLoginBinding
import com.example.foodfusion.model.usermodel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val googleSigninOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        // initilization
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        googleSignInClient = GoogleSignIn.getClient(this,googleSigninOptions)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.dontHaveAccount.setOnClickListener {
            startActivity(Intent(this,SignActivity::class.java))

        }
        binding.loginbutton.setOnClickListener {
            email = binding.editTextTextEmailAddress.text.toString().trim()
            password = binding.editTextTextPassword.text.toString().trim()
            if (email.isBlank()||password.isBlank()){
                Toast.makeText(this, "please enter all details", Toast.LENGTH_SHORT).show()
            }else{
                createuser(email,password)
            }
        }
        binding.googleebutton.setOnClickListener {
            val signintent = googleSignInClient.signInIntent
            launcher.launch(signintent)

        }

    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account: GoogleSignInAccount? = task.result
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

    private fun createuser(email: String, password: String) {
auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
    if (task.isSuccessful){
        Toast.makeText(this, "User Sign-In Successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }else{
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show()
                saveuesrdata()
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                Toast.makeText(this, "Sign-in Failed", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
    }

    private fun saveuesrdata() {
        email = binding.editTextTextEmailAddress.text.toString().trim()
        password = binding.editTextTextPassword.text.toString().trim()
        val user = usermodel("",email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }
}