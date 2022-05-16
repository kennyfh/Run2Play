package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
// import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityRetosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reto_running.view.*
import kotlinx.android.synthetic.main.inforetos_dialog_box.view.*


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
        db.collection("challenges").orderBy("challengeNumber").get()
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

                    framAchiev.setOnClickListener {
                        val view = layoutInflater.inflate(R.layout.inforetos_dialog_box, null)
                        val intent = Intent(applicationContext,RetoRunningActivity::class.java)
                        intent.putExtra("title",document.data?.get("title")?.toString())
                        intent.putExtra("description",document.data?.get("description")?.toString())
                        intent.putExtra("distance",document.data?.get("distance")?.toString())
                        intent.putExtra("time",document.data?.get("time")?.toString())
                        intent.putExtra("reward",document.data?.get("reward")?.toString())
                        intent.putExtra("unlockAchievement",document.data?.get("unlockAchievement")?.toString())
                        intent.putExtra("challengeNumber",document.data?.get("challengeNumber")?.toString())


                        startActivity(intent)
                    }

                    cnt += 1
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }


    }

    override fun onResume() {
        super.onResume()
        binding.fragmentContainerView2.getFragment<MarcadorFragment>().checkCurrencyOne(checkUser())
        binding.fragmentContainerView2.getFragment<MarcadorFragment>().checkCurrencyTwo(checkUser())

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