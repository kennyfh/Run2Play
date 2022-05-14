package com.google.codelabs.buildyourfirstmap

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityLogrosBinding
import com.google.codelabs.buildyourfirstmap.databinding.ActivityShopBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Comment
import java.io.File

class ShopActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShopBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val uId = checkUser()





        getShop()



    }


    private fun getShop(){

        var userName = checkUser()

        val db = FirebaseFirestore.getInstance()
        db.collection("items").orderBy("id").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    Log.w("item", "${document["id"]} => omg")

                    val lLay = binding.shopList.getChildAt(
                        document.data["id"].toString().toInt() - 1
                    ) as LinearLayout
                    val tViwy = lLay.getChildAt(1) as TextView
                    //val tImg = lLay.getChildAt(0) as ImageView
                    tViwy.text = document.data?.get("title")?.toString()
                    val tImg = lLay.getChildAt(0) as ImageView
                    Log.w("achiev", "gonna set image ${document.data["id"]} => omg")
                    val imageName = document.data["image"]
                    val storageRef =
                        FirebaseStorage.getInstance().reference.child("Images/Items/$imageName")

                    val localfile = File.createTempFile("tempImage", "png")
                    storageRef.getFile(localfile).addOnSuccessListener {

                        val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                        tImg.setImageBitmap(bitmap)

                    }
                }
            }


            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }


    }






        private fun checkUser(): String {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){

            //user not null,user is logged in, get user info
            val uid = firebaseUser.uid
            //set to text view
            return uid.toString()


        }
        else {


            return ""
        }

    }


}