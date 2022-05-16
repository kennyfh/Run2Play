package com.google.codelabs.buildyourfirstmap

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.databinding.ActivityInventoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)



        firebaseAuth = FirebaseAuth.getInstance()
        val uId = checkUser()
        val db = FirebaseFirestore.getInstance()


        db.collection("userItems").whereEqualTo("user", uId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("items").whereEqualTo("id", document.data["item"]).get()
                        .addOnSuccessListener {
                            items ->
                            for(item in items){
                                generateCard(item.data["title"].toString(),document.data["amount"].toString(),item.data["image"].toString())
                            }
                        }
                    Log.w("inventory", "Lets go new item ")


                }
            }
            .addOnFailureListener { exception ->
                Log.w("tag", "Error getting documents ", exception)

            }

    }

    private fun generateCard(title: String, amount: String, path: String) {
        val linear = LinearLayout(this)
        val layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, // LinearLayout width
            LayoutParams.WRAP_CONTENT // LinearLayout height
        )

        linear.layoutParams = layoutParams
        linear.orientation = LinearLayout.VERTICAL

        linear.setOnClickListener{
            Toast.makeText(
                applicationContext,
                "Layout cliqueada.",
                Toast.LENGTH_SHORT).show()
        }

        // generamos image view
        val imageView = ImageView(this)
        val r: Resources = resources
        var px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100f, r.getDisplayMetrics()
            )
        )
        val params = LayoutParams(px,px)
        imageView.layoutParams = params

        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/Items/$path")

        val localfile = File.createTempFile("tempImage", "png")
        storageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imageView.setImageBitmap(bitmap)

        }

        //Generamos TextView
        val quote = TextView(this)
        val layoutParams2 = LayoutParams(
            LayoutParams.WRAP_CONTENT, // LinearLayout width
            LayoutParams.WRAP_CONTENT // LinearLayout height
        )
        quote.layoutParams = layoutParams2
        quote.text = title + " x " + amount;
//        quote.textSize = 24f
        px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 90f, r.getDisplayMetrics()
            )
        )
        quote.maxWidth = px
        quote.textAlignment = View.TEXT_ALIGNMENT_CENTER
        quote.setTextColor(Color.BLACK)
        quote.setTypeface(Typeface.SANS_SERIF,Typeface.NORMAL)

        linear.addView(imageView)
        linear.addView(quote)
        binding.layoutInventory.addView(linear)

    }



    private fun generateImageView(path: String) :ImageView{
        val imageView = ImageView(this)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT,350)
        imageView.layoutParams = params
        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/Items/$path")

        val localfile = File.createTempFile("tempImage", "png")
        storageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imageView.setImageBitmap(bitmap)

        }
        //imageView.setImageResource(R.drawable.common_google_signin_btn_icon_dark_focused)
        //imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return imageView
    }

    private fun checkUser(): String {
        //check user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        Log.w("tag", "Error getting documents  ${firebaseUser?.uid}")
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