package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.adapter.RetoAdapter
import com.google.codelabs.buildyourfirstmap.databinding.ActivityRetosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_reto_running.view.*
import kotlinx.android.synthetic.main.inforetos_dialog_box.view.*
import kotlinx.android.synthetic.main.item_reto.*
import org.w3c.dom.Text
import java.io.File

class RetosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRetosBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val uId = checkUser()

        fillChallenges(uId)
        /*var tablAchiev = binding.challengesTable.getChildAt(0) as LinearLayout
        var framAchiev = tablAchiev.getChildAt(0) as FrameLayout
        var titleBind = framAchiev.getChildAt(1) as TextView
        titleBind.text = "Kenny Jesús"

        val new1 = hashMapOf(
            "description" to "Descripcion random",
            "distance" to 0,
            "reward" to 0,
            "time" to 0,
            "title" to "Titulo random",
            "unlockAchievement" to true


        )

       val new2 = hashMapOf(
           "description" to "La tercera descripcion",
           "distance" to 0,
           "reward" to 0,
           "time" to 0,
           "title" to "Tercer titulo",
           "unlockAchievement" to true


       )

       val new3 = hashMapOf(
           "description" to "La cuarta descripcion",
           "distance" to 0,
           "reward" to 0,
           "time" to 0,
           "title" to "Cuarto titulo",
           "unlockAchievement" to true


       )

       val new4 = hashMapOf(
           "description" to "La quinta descripcion",
           "distance" to 0,
           "reward" to 0,
           "time" to 0,
           "title" to "Quinto titulo",
           "unlockAchievement" to true


       )


       val db = FirebaseFirestore.getInstance()
       db.collection("challenges").document().set(new1)
           .addOnSuccessListener { Log.d("tag", "Se ha metido un nuevo usuario lets goo") }
           .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu")  }
       db.collection("challenges").document().set(new2)
           .addOnSuccessListener { Log.d("tag", "Se ha metido un nuevo usuario lets goo") }
           .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu")  }
       db.collection("challenges").document().set(new3)
           .addOnSuccessListener { Log.d("tag", "Se ha metido un nuevo usuario lets goo") }
           .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu")  }
       db.collection("challenges").document().set(new4)
           .addOnSuccessListener { Log.d("tag", "Se ha metido un nuevo usuario lets goo") }
           .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu")  }

*/
        //initRecyclerView()
        /*val recyclerView = findViewById<RecyclerView>(R.id.recyclerRetos)
        val rChild = recyclerView.getChildAt(0) as androidx.cardview.widget.CardView
        val rSupChild = rChild.getChildAt(0) as LinearLayout
        val rSupSupChild = rSupChild.getChildAt(0) as RelativeLayout
        val rMegaChild = rSupSupChild.getChildAt(1) as LinearLayout
        val rOmegaChild = rMegaChild.getChildAt(0) as TextView
        rOmegaChild.text = "omg super cute"*/

        val buttonInfo = findViewById<ImageButton>(R.id.buttonInfo)





        // Initialize and assign variable
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set Home selected
        bottomNavigation.selectedItemId = R.id.retos;


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

    private fun fillChallenges(uId: String) {
        val db = FirebaseFirestore.getInstance()
        var cnt = 0
        db.collection("challenges").get()
            .addOnSuccessListener { documents ->
                for (document in documents){

                    var tablAchiev = binding.challengesTable.getChildAt(cnt) as LinearLayout
                    var framAchiev = tablAchiev.getChildAt(0) as FrameLayout
                    var titleBind = framAchiev.getChildAt(1) as TextView
                    titleBind.text = document.data?.get("title")?.toString()
                    var descripBind = framAchiev.getChildAt(2) as TextView
                    descripBind.text = document.data?.get("description")?.toString()
                    var infButton = framAchiev.getChildAt(4) as ImageButton

                    infButton.setOnClickListener{
                        //asignando valores
                        val builder = AlertDialog.Builder(this@RetosActivity)
                        val view = layoutInflater.inflate(R.layout.inforetos_dialog_box, null)

                        //pasando la vista al builder
                        builder.setView(view)

                        //creando dialog
                        val dialog = builder.create()
                        dialog.show()

                        // cerrar dialog
                        val btnExit = view.imageBtnExitInfo
                        btnExit.setOnClickListener {
                            dialog.dismiss()
                        }

                        view.infoTituloReto.text = document.data?.get("title")?.toString()
                        view.infoDescripcionReto.text = document.data?.get("description")?.toString()
                        view.infoDistanciaReto.text = document.data?.get("distance")?.toString()
                        view.infoTiempoReto.text = document.data?.get("time")?.toString()
                        view.infoRecompensaReto.text = document.data?.get("reward")?.toString()

                    }

                    cnt += 1
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }


    }
/*
    private fun initRecyclerView(){
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerRetos)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter= RetoAdapter(RetoProvider.retoList)

    }*/

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