package com.google.codelabs.buildyourfirstmap

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityLogrosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.protobuf.Empty
import kotlinx.android.synthetic.main.achievement_pop_up.view.*
import kotlinx.android.synthetic.main.inforetos_dialog_box.view.*
import org.w3c.dom.Comment
import java.io.File

class LogrosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogrosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val uId = checkUser()

        checkName(uId)



        getUserData()



        // Initialize and assign variable
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigation.selectedItemId = R.id.logros;

        // Perform item selected listener
        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.retos -> {
                    startActivity(Intent(applicationContext, RetosActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnItemSelectedListener true
                }
                R.id.logros -> {
                    startActivity(Intent(applicationContext, LogrosActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnItemSelectedListener true
                }
                R.id.mapa -> {
                    startActivity(Intent(applicationContext, MapaActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@OnItemSelectedListener true
                }
            }
            false
        })
    }


    private fun getUserData(){

        var userName = checkUser()
        val db = FirebaseFirestore.getInstance()
        db.collection("achievements").get()
            .addOnSuccessListener { documents ->
                for (document in documents){

                    Log.w("achiev", "${document["achievementNumber"]} => omg")



                    db.collection("userAchievements").whereEqualTo("mix",userName+document.id).get().addOnSuccessListener {
                            users ->
                        if(users.isEmpty){

                            generateAchievement(document.data["title"].toString(),document.data["description"].toString(),document.data["rewardCurrencyOne"].toString(),document.data["iconInactive"].toString())



                        }
                        else{

                            generateAchievement(document.data["title"].toString(),document.data["description"].toString(),document.data["rewardCurrencyOne"].toString(),document.data["iconActive"].toString())

                        }

                    }
                        .addOnFailureListener { exception->
                            Log.w("tag", "Error getting documents " , exception) }
                }

            }.addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }
    }

    private fun generateAchievement(title: String, description: String, rewardOne: String, path: String) {

        val linear = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // LinearLayout width
            LinearLayout.LayoutParams.WRAP_CONTENT // LinearLayout height
        )

        linear.layoutParams = layoutParams
        linear.orientation = LinearLayout.VERTICAL
        val storageRef =
            FirebaseStorage.getInstance().reference.child("Images/$path")

        val localfile = File.createTempFile("tempImage", "png")

        // generamos image view
        val imageView = ImageView(this)
        val r: Resources = resources
        var px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 100f, r.getDisplayMetrics()
            )
        )
        val params = LinearLayout.LayoutParams(px, px)
        imageView.layoutParams = params


        storageRef.getFile(localfile).addOnSuccessListener {

            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imageView.setImageBitmap(bitmap)
            //Generamos TextView
            val quote = TextView(this)
            val layoutParams2 = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // LinearLayout width
                LinearLayout.LayoutParams.WRAP_CONTENT // LinearLayout height
            )
            quote.layoutParams = layoutParams2
            quote.text = title;
//        quote.textSize = 24f
            px = Math.round(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 90f, r.getDisplayMetrics()
                )
            )
            quote.maxWidth = px
            quote.textAlignment = View.TEXT_ALIGNMENT_CENTER
            quote.setTextColor(Color.BLACK)
            quote.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL)
            linear.gravity = Gravity.CENTER

            linear.addView(imageView)
            linear.addView(quote)
            binding.achievementsTable.addView(linear)

        }





        //PULSAR LOGRO PARA POP-UP
        linear.setOnClickListener {
            //asignando valores
            val builder = AlertDialog.Builder(this@LogrosActivity)
            val view = layoutInflater.inflate(R.layout.achievement_pop_up, null)

            //pasando la vista al builder
            builder.setView(view)

            //creando dialog
            val dialog = builder.create()
            dialog.show()

            // cerrar dialog
            val btnExit = view.btn_pop_up_exit
            btnExit.setOnClickListener {
                dialog.dismiss()
            }




            storageRef.getFile(localfile).addOnSuccessListener {

                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                view.image_pop_Logro.setImageBitmap(bitmap)
            }

            view.logro_pop_Title.text = title
            view.logro_pop_description.text = description
            view.logro_pop_reward.text = rewardOne

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

    private fun checkName(userName: String){

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var name = ""

        db.collection("users").whereEqualTo("uId",userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    binding.nombrePerfil.text = document.data["username"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }



    }

}