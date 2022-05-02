package com.google.codelabs.buildyourfirstmap;

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.codelabs.buildyourfirstmap.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var  binding: ActivitySignUpBinding

    //ActionBar
    //private lateinit var actionBar: ActionBar

    //ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth

    private var email = ""
    private var password = ""
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure ActionBar
        //actionBar = supportActionBar!!
       // actionBar.title = "Sign Up"
       // actionBar.setDisplayHomeAsUpEnabled(true)
       // actionBar.setDisplayShowHomeEnabled(true)

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Creating account...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //handle click, begin signup
        binding.singUpBtn.setOnClickListener{
            //validate data
            validateData()
        }

    }

    private fun validateData() {
        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        username = binding.usernameEt.text.toString().trim()

        //validate data
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            //invalid email format
            binding.emailEt.error = "Invalid email format"

        }
        else if (TextUtils.isEmpty(password)){
            //password isnt entered
            binding.passwordEt.error = "Please enter password"

        }
        else if(password.length <6){

            //password length is less than 6
            binding.passwordEt.error = "Password must atleast be 6 characters long"
        }
        else if(username.length<4){
            binding.passwordEt.error = "Username must atleast be 4 characters long"
        }
        else{

            //data is valid, continue signup
            firebaseSignUp()
        }
    }

    private fun firebaseSignUp() {
        //show progress
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                //signup success
                progressDialog.dismiss()
                //get current user
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"Account created with email $email", Toast.LENGTH_SHORT).show()
                createCustomUser(firebaseUser)
                //open profile
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { e->
                //signup failed
                progressDialog.dismiss()
                Toast.makeText(this,"SignUp Failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
    private fun createCustomUser(firebaseUser: FirebaseUser) {

        val user = hashMapOf(
            "currencyOne" to 0,
            "currencyTwo" to 0,
            "mail" to email,
            "uId" to firebaseUser!!.uid,
            "username" to username

        )
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document().set(user)
            .addOnSuccessListener { Log.d("tag", "Se ha metido un nuevo usuario lets goo")}
            .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu") }



    }
    override fun onSupportNavigateUp(): Boolean {

        onBackPressed() //go back to previous activity, when back button of actiobar clicked
        return super.onSupportNavigateUp()
    }
}