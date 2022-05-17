package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.codelabs.buildyourfirstmap.databinding.ActivityShopBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.inforetos_dialog_box.view.*
import kotlinx.android.synthetic.main.shop_pop_up.view.*
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

    private fun generateItemShop(
        title: String,
        description: String,
        path: String,
        bitPrice: Int,
        diamondPrice: Int,
        db: FirebaseFirestore,
        itemId : String
    ) {
        val linear = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // LinearLayout width
            LinearLayout.LayoutParams.WRAP_CONTENT // LinearLayout height
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
        val params = LinearLayout.LayoutParams(px, px)
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
        val layoutParams2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, // LinearLayout width
            LinearLayout.LayoutParams.WRAP_CONTENT // LinearLayout height
        )
        quote.layoutParams = layoutParams2
        quote.text = title
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

        linear.setOnClickListener{
            //asignando valores
            val builder = AlertDialog.Builder(this@ShopActivity)
            val view = layoutInflater.inflate(R.layout.shop_pop_up, null)

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

            view.shop_title.text = title
            view.shop_description.text = description
            storageRef.getFile(localfile).addOnSuccessListener {

                val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                view.shop_img.setImageBitmap(bitmap)

            }

            if(bitPrice>0){
                view.price.text = bitPrice.toString()
                view.iconShop.setImageResource(R.drawable.ic_baseline_currency_bitcoin_24)
                view.iconShop.setBackgroundColor(Color.parseColor("#E0A633"))
                view.iconShop.imageTintMode = PorterDuff.Mode.ADD
                view.setBackgroundResource(R.drawable.style_bordes_text)


            }
            else{
                view.price.text = diamondPrice.toString()
                view.iconShop.setImageResource(R.drawable.ic_baseline_diamond_24)
                view.iconShop.setBackgroundColor(Color.parseColor("#2196F3"))
                view.iconShop.imageTintMode = PorterDuff.Mode.ADD
                view.setBackgroundResource(R.drawable.style_bordes_text)
            }

            view.buyButton.setOnClickListener{
                dialog.dismiss()

                db.collection("users").whereEqualTo("uId", checkUser()).get()
                    .addOnSuccessListener { users ->
                        for (user in users) {
                            //Log.w("reward", "Has ganado recompensas segundarias ")
                            //binding.fragmentContainerView3.getFragment<MarcadorFragment>().checkCurrencyTwo(checkUser())
                            var currentCurrencyOne = user.data["currencyOne"].toString().toInt()
                            var currentCurrencyTwo = user.data["currencyTwo"].toString().toInt()
                            if(bitPrice>0 && currentCurrencyOne>=bitPrice){

                                //se puede realizar una doble query de forma que solo salgan los elementos necesarios

                                db.collection("users").document(user.id).update("currencyOne",currentCurrencyOne-bitPrice)
                                db.collection("userItems").whereEqualTo("user", checkUser())
                                    .whereEqualTo("item",itemId).get()
                                    .addOnSuccessListener { items ->

                                        if (items.isEmpty){

                                            val userItem = hashMapOf(
                                                "amount" to "1",
                                                "item" to itemId,
                                                "user" to checkUser()

                                            )

                                            db.collection("userItems").document().set(userItem)
                                                .addOnSuccessListener { Log.d("shop", "Se ha comprado algo el niño")}
                                                .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu") }

                                        }
                                        else{
                                            for(item in items){
                                                db.collection("userItems").document(item.id).update("amount",(item.data["amount"].toString().toInt()+1).toString())
                                            }
                                        }
                                        binding.fragmentContainerView.getFragment<MarcadorFragment>().checkCurrencyOne(checkUser())

                                    }
                            }

                            else{

                                db.collection("users").document(user.id).update("currencyTwo",currentCurrencyTwo-diamondPrice)
                                db.collection("userItems").whereEqualTo("user", checkUser())
                                    .whereEqualTo("item",itemId).get()
                                    .addOnSuccessListener { items ->

                                        if (items.isEmpty){

                                            val userItem = hashMapOf(
                                                "amount" to "1",
                                                "item" to itemId,
                                                "user" to checkUser()

                                            )

                                            db.collection("userItems").document().set(userItem)
                                                .addOnSuccessListener {

                                                    Log.d("shop", "Se ha comprado algo el niño")

                                                }
                                                .addOnFailureListener { e -> Log.w("tag", "Error, no se mete en la base de datos uwu") }

                                        }
                                        else{
                                            for(item in items){
                                                db.collection("userItems").document(item.id).update("amount",(item.data["amount"].toString().toInt()+1).toString())
                                            }


                                        }
                                        binding.fragmentContainerView.getFragment<MarcadorFragment>().checkCurrencyTwo(checkUser())

                                    }

                            }




                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("tag", "Error getting documents ", exception)

                    }
            }






        }

        linear.addView(imageView)
        linear.addView(quote)
        binding.shopList.addView(linear)

    }


    private fun getShop(){

        var userName = checkUser()

        val db = FirebaseFirestore.getInstance()
        db.collection("items").orderBy("id").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                    Log.w("item", "${document["id"]} => omg")
                    generateItemShop(document.data["title"].toString(),document.data["description"].toString(),document.data["image"].toString()
                    ,document.data["bitPrice"].toString().toInt(),document.data["diamondPrice"].toString().toInt(),db
                    ,document.data["id"].toString())

                    /*val lLay = binding.shopList.getChildAt(
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

                    }*/
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