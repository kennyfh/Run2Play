package com.google.codelabs.buildyourfirstmap;

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PatternMatcher
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
//import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.codelabs.buildyourfirstmap.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding:ActivityLoginBinding
    //ActionBar
    //private lateinit var actionBar: ActionBar
    //ProgresDialog
    private lateinit var progressDialog: ProgressDialog
    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth
    private var email =""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure actionbar
       // actionBar = supportActionBar!!
        //actionBar.title = "Login"

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setMessage("Loggin In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, open register activity

        binding.noAccountTv.setOnClickListener{

            startActivity(Intent(this,SignUpActivity::class.java))
        }

        //handle click, begin login
        binding.loginBtn.setOnClickListener{

            //before loggin in, validate data
            validateData()

        }
    }

    private fun validateData() {

        //get data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        //validate data
        //invalid email format
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            binding.emailEt.error = "Invalid email format"

        else if (TextUtils.isEmpty(password)){

            //no password entered
            binding.passwordEt.error = "Please enter password"
        }
        else {
            //data is validated, begin login
            firebaseLogin()
        }

    }

    private fun firebaseLogin() {
        //show progress
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                //login success
                progressDialog.dismiss()
                //get user info
                val firebaseUser = firebaseAuth.currentUser
                val email = firebaseUser!!.email
                Toast.makeText(this,"LoggedIn as $email" , Toast.LENGTH_SHORT).show()

                //open profile
                startActivity(Intent(this,MainActivity::class.java))
                finish()

        }
            .addOnFailureListener { e->
                //login failed
                progressDialog.dismiss()
                Toast.makeText(this,"Login failed due to ${e.message}" , Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkUser() {

        //if user is already logged in go to profile activity
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //user is logged in
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}