package com.google.codelabs.buildyourfirstmap;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.ActionBar
import com.google.firebase.auth.FirebaseAuth
import com.google.codelabs.buildyourfirstmap.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    //ViewBinding
    private lateinit var binding: ActivityProfileBinding
    //ActionBar
    //private lateinit var actionBar: ActionBar
    //FirebaseAuth
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configure ActionBar
       // actionBar = supportActionBar!!
       // actionBar.title = "Profile"

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle click, logout
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }
    }

    private fun checkUser() {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){

            //user not null,user is logged in, get user info
            val email = firebaseUser.email
            //set to text view
            binding.emailTv.text = email


        }
        else{
            //user is null, user is not loggedin, goto login activity
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}