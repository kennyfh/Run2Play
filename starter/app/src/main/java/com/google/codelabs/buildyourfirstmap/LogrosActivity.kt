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
        var cnt = 1
        var userName = checkUser()
        val db = FirebaseFirestore.getInstance()
        db.collection("achievements").orderBy("achievementNumber").get()
            .addOnSuccessListener { documents ->
                for (document in documents){

                    Log.w("achiev", "${document["achievementNumber"]} => omg")

                    val lLay = binding.achievementsTable.getChildAt(document.data["achievementNumber"].toString().toInt()-1) as LinearLayout
                    val tViwy = lLay.getChildAt(1) as TextView
                    //val tImg = lLay.getChildAt(0) as ImageView
                    tViwy.text = document.data?.get("title")?.toString()

                    db.collection("userAchievements").whereEqualTo("mix",userName+document.id).get().addOnSuccessListener {
                            users ->
                        if(users.isEmpty){

                            Log.w("achiev", "El logro $cnt con valor ir ${document.data["achievementNumber"]} no forma parte de los logros obtenidos => omg")
                            val iLay = binding.achievementsTable.getChildAt(document.data["achievementNumber"].toString().toInt()-1) as LinearLayout
                            val tImg = iLay.getChildAt(0) as ImageView
                            Log.w("achiev", "gonna set inactive ${document.data["achievementNumber"]} => omg")
                            val imageName = document.data["iconInactive"]
                            val storageRef =
                                FirebaseStorage.getInstance().reference.child("Images/$imageName")

                            val localfile = File.createTempFile("tempImage", "png")
                            storageRef.getFile(localfile).addOnSuccessListener {

                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                tImg.setImageBitmap(bitmap)

                            }





                            //PULSAR LOGRO PARA POP-UP
                            lLay.setOnClickListener {
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

                                view.logro_pop_Title.text = document.data?.get("title")?.toString()
                                view.logro_pop_description.text = document.data?.get("description")?.toString()
                                view.logro_pop_reward.text = document.data?.get("rewardCurrencyOne")?.toString()

                            }

                        }
                        else{

                            val iLay = binding.achievementsTable.getChildAt(document.data["achievementNumber"].toString().toInt()-1) as LinearLayout
                            val tImg = iLay.getChildAt(0) as ImageView
                            Log.w("achiev", "gonna set active ${document.data["achievementNumber"]} => omg")
                            val imageName = document.data["iconActive"]
                            val storageRef =
                                FirebaseStorage.getInstance().reference.child("Images/$imageName")

                            val localfile = File.createTempFile("tempImage", "png")
                            storageRef.getFile(localfile).addOnSuccessListener {

                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                                tImg.setImageBitmap(bitmap)

                            }

                            //PULSAR LOGRO PARA POP-UP
                            lLay.setOnClickListener {
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

                                view.logro_pop_Title.text = document.data?.get("title")?.toString()
                                view.logro_pop_description.text = document.data?.get("description")?.toString()
                                view.logro_pop_reward.text = document.data?.get("rewardCurrencyOne")?.toString()

                            }
                        }

                    }
                        .addOnFailureListener { exception->
                            Log.w("tag", "Error getting documents " , exception) }
                }

            }.addOnFailureListener { exception->
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