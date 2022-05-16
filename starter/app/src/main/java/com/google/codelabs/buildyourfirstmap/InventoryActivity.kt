package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityInventoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import android.widget.LinearLayout.LayoutParams

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        generateCard()
        generateCard()
        generateCard()
        generateCard()
        generateCard()
        generateCard()



        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
//        val uId = checkUser()

//        checkName(uId)

//        getUserData()



//        // Initialize and assign variable
//        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//
//        // Set Home selected
//        bottomNavigation.selectedItemId = R.id.logros;
//
//        // Perform item selected listener
//        bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.retos -> {
//                    startActivity(Intent(applicationContext, RetosActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    return@OnItemSelectedListener true
//                }
//                R.id.logros -> {
//                    startActivity(Intent(applicationContext, InventoryActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    return@OnItemSelectedListener true
//                }
//                R.id.mapa -> {
//                    startActivity(Intent(applicationContext, MapaActivity::class.java))
//                    overridePendingTransition(0, 0)
//                    return@OnItemSelectedListener true
//                }
//            }
//            false
//        })
    }
// TODO Arreglar tarjeta para que se vea bonita
    private fun generateCard() {
        val card_view = CardView(this)
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, // CardView width
            LayoutParams.WRAP_CONTENT // CardView height
        )

        // Set bottom margin for card view
        layoutParams.bottomMargin = 50

        // Set the card view layout params
        card_view.layoutParams = layoutParams

        // Set the card view corner radius
        card_view.radius = 12F

        // Set the card view content padding
        card_view.setContentPadding(25,25,25,25)

        // Set the card view background color
        card_view.setCardBackgroundColor(Color.LTGRAY)

        // Set card view elevation
        card_view.cardElevation = 8F

        // Set card view maximum elevation
        card_view.maxCardElevation = 12F

        // AQUI IRÁ EL POPUP
        card_view.setOnClickListener{
            Toast.makeText(
                applicationContext,
                "Card clicked.",
                Toast.LENGTH_SHORT).show()
        }

        val quote = TextView(this)
        quote.text = "\"Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.\n";
        quote.textSize = 24f
        quote.setTextColor(Color.WHITE)
        quote.setTypeface(Typeface.SANS_SERIF,Typeface.NORMAL)
        card_view.addView(quote)
        // Add an ImageView to the CardView
//        card_view.addView(generateImageView())
        binding.layoutInventory.addView(card_view)

    }

    private fun generateImageView() :ImageView{
        val imageView = ImageView(this)
        val params = LayoutParams(LayoutParams.WRAP_CONTENT,350)
        imageView.layoutParams = params
        imageView.setImageResource(R.drawable.ic_baseline_map_24)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return imageView
    }


//    private fun getUserData(){
//        var cnt = 0
//        var cntImage = 0
//        var userName = "IGuowIU1wuPt6SjUiKDfXpFXk6K2"
//        val logName = "X48lZc4Uh3gLT7FWnhFl"
//        val db = FirebaseFirestore.getInstance()
//        db.collection("achievements").orderBy("achievementNumber").get()
//            .addOnSuccessListener { documents ->
//                for (document in documents){
//                    Log.w("achiev", "${document["achievementNumber"]} => omg")
//
//                    val lLay = binding.achievementsTable.getChildAt(cnt) as LinearLayout
//                    val tViwy = lLay.getChildAt(1) as TextView
//                    //val tImg = lLay.getChildAt(0) as ImageView
//                    tViwy.text = document.data?.get("title")?.toString()
//
//                    db.collection("userAchievements").whereEqualTo("mix",userName+document.id).get().addOnSuccessListener {
//                        users ->
//
//                        if (users.isEmpty){
//                            val iLay = binding.achievementsTable.getChildAt(cntImage) as LinearLayout
//                            val tImg = iLay.getChildAt(0) as ImageView
//                            Log.w("achiev", "gonna set inactive ${cntImage+1} => omg")
//                            val imageName = "iconInactive" + (cntImage + 1) + ".png"
//                            val storageRef =
//                                FirebaseStorage.getInstance().reference.child("Images/$imageName")
//
//                            val localfile = File.createTempFile("tempImage", "png")
//                            storageRef.getFile(localfile).addOnSuccessListener {
//
//                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//                                tImg.setImageBitmap(bitmap)
//
//                            }
//
//                            cntImage += 1
//
//                        }
//
//                        else{
//                            Log.w("achiev", "gonna set active ${cntImage+1} => omg")
//                            val iLay = binding.achievementsTable.getChildAt(cntImage) as LinearLayout
//                            val tImg = iLay.getChildAt(0) as ImageView
//                            val imageName = "iconActive" + (cntImage + 1) + ".png"
//                            val storageRef =
//                                FirebaseStorage.getInstance().reference.child("Images/$imageName")
//
//                            val localfile = File.createTempFile("tempImage", "png")
//                            storageRef.getFile(localfile).addOnSuccessListener {
//
//                                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
//                                tImg.setImageBitmap(bitmap)
//
//                            }
//
//                            cntImage += 1
//                        }
//
//
//
//                    }
//
//
//
//
//                    cnt += 1
//
//
//
//
//
//                }
//            }
//            .addOnFailureListener { exception->
//                Log.w("tag", "Error getting documents " , exception)
//
//            }

       // firebaseAuth = FirebaseAuth.getInstance()
        //val firebaseUser = firebaseAuth.currentUser
        //val uId = checkUser()
       /* database = FirebaseDatabase.getInstance().getReference("achievements")
        //checkName(uId)
        //Log.w("tag", "ya esta aqui ya llego => he entrado en la funcion} ")
        database.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        Log.w("tag", "ya esta aqui ya llego => ${userSnapshot.toString()} ")
                        //val logro = userSnapshot.getValue(Logro::class.java)
                        //logrosList += logro!!
                        binding.tvLogro1.text = "surya la sury"

                    }
                    //Log.w("tag", "ya esta aqui ya llego => he entrado en la funcion 2} ")
                    //logrosRecyclerView.adapter = LogrosAdapter(logrosList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })*/
//    }




    /*private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerLogros)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        var kyara = LogrosAdapter(logrosProvider.logritos)
        val handler = Handler()
        handler.postDelayed({
            // your code to run after 2 second
            recyclerView.adapter = kyara
        }, 2000)


    }*/





//        private fun checkUser(): String {
//        //check user is logged in or not
//        val firebaseUser = firebaseAuth.currentUser
//        if(firebaseUser != null){
//
//            //user not null,user is logged in, get user info
//            val uid = firebaseUser.uid
//            //set to text view
//            return uid.toString()
//
//
//        }
//        else {
//
//
//            return ""
//        }
//
//    }

//    private fun checkName(userName: String){
//
//        //llamamos a la base de datos
//        val db = FirebaseFirestore.getInstance()
//        //sacamos la colección usuarios para obtener los elementos buscados
//
//
//
//        var name = ""
//
//        db.collection("users").whereEqualTo("uId",userName).get()
//            .addOnSuccessListener { documents ->
//                for (document in documents){
//                    binding.nombrePerfil.text = document.data["username"].toString()
//
//                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
//                }
//            }
//            .addOnFailureListener { exception->
//                Log.w("tag", "Error getting documents " , exception)
//
//            }
//
//
//
//    }

}