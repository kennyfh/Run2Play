package com.google.codelabs.buildyourfirstmap

// import androidx.recyclerview.widget.RecyclerView
import android.animation.LayoutTransition
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginRight
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.codelabs.buildyourfirstmap.databinding.ActivityRetosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_reto_running.view.*
import kotlinx.android.synthetic.main.inforetos_dialog_box.view.*
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

    private fun generateChallenge(title: String, description: String, reward: String, time: String, distance: String, unlockAchievement: String, challengeNumber: String) {
       //LINEAR LAYOUT
        val linear = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // LinearLayout width
            LinearLayout.LayoutParams.WRAP_CONTENT // LinearLayout height

        )

        linear.layoutParams = layoutParams
        linear.setBackgroundResource(R.drawable.color_gradient_retos)
        linear.orientation = LinearLayout.VERTICAL
        val lt = LayoutTransition()
        lt.disableTransitionType(LayoutTransition.DISAPPEARING)
        linear.layoutTransition = lt

        //FRAME LAYOUT

        val frame = FrameLayout(this)
        val frameParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val r: Resources = resources
        var px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 6f, r.getDisplayMetrics()
            )
        )
        frame.setPadding(px,px,px,px)







        // generamos image view
        val imageView = ImageView(this)

         px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 70f, r.getDisplayMetrics()
            )
        )
        val params = FrameLayout.LayoutParams(px, px)

        imageView.layoutParams = params

        px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 14f, r.getDisplayMetrics()
            )
        )
        imageView.setPadding(px,0,px,0)
        imageView.setImageResource(R.drawable.ic_baseline_directions_run_24)





        //Generamos TextView title
        val quote = TextView(this)
        val layoutParams2 = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // LinearLayout width
            LinearLayout.LayoutParams.MATCH_PARENT // LinearLayout height
        )
        layoutParams2.gravity = Gravity.CENTER
        quote.layoutParams = layoutParams2
        quote.text = title
        quote.setTextColor(Color.BLACK)
        px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f, r.getDisplayMetrics()
            )
        )

        quote.textSize = px.toFloat()
        quote.gravity = Gravity.CENTER

        quote.setTypeface(quote.getTypeface(), Typeface.BOLD)


//

        val button = ImageButton(this)
        var layoutParams3 = FrameLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // LinearLayout width

            LinearLayout.LayoutParams.WRAP_CONTENT // LinearLayout height
        )


        layoutParams3.gravity = Gravity.RIGHT or Gravity.CENTER

        px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f, r.getDisplayMetrics()
            )
        )
        layoutParams3.setMargins(0,0,px,0)
        button.layoutParams = layoutParams3






        button.setBackgroundResource(R.drawable.style_relleno_text)
        px = Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 40f, r.getDisplayMetrics()
            )
        )
        button.minimumWidth = px
        button.minimumHeight = px
        button.setImageResource(R.drawable.ic_baseline_info_24)

        button.setOnClickListener{
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

            view.infoTituloReto.text = title
            view.infoDescripcionReto.text = description
            view.infoDistanciaReto.text = distance
            view.infoTiempoReto.text = time
            view.infoRecompensaReto.text = reward

        }

        frame.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.inforetos_dialog_box, null)
            val intent = Intent(applicationContext,RetoRunningActivity::class.java)
            intent.putExtra("title",title)
            intent.putExtra("description",description)
            intent.putExtra("distance",distance)
            intent.putExtra("time",time)
            intent.putExtra("reward",reward)
            intent.putExtra("unlockAchievement",unlockAchievement)
            intent.putExtra("challengeNumber",challengeNumber)


            startActivity(intent)
        }





        frame.addView(imageView)
        frame.addView(quote)
        frame.addView(button)

        linear.addView(frame)
        binding.challengesTable.addView(linear)

    }

    private fun fillChallenges(uId: String) {
        val db = FirebaseFirestore.getInstance()
        var cnt = 0
        db.collection("challenges").orderBy("challengeNumber").get()
            .addOnSuccessListener { documents ->
                for (document in documents){


                generateChallenge(document.data["title"].toString(),document.data["description"].toString(),document.data["reward"].toString(),
                    document.data["time"].toString(),document.data["distance"].toString(),document.data["unlockAchievement"].toString(),document.data["challengeNumber"].toString())



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