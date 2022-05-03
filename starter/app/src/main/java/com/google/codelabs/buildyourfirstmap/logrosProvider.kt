package com.google.codelabs.buildyourfirstmap

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

object logrosProvider {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    var logrosList = listOf<Logro>()
    var logritos = checkUser()



    fun checkUser(): List<Logro> {
        //se le puede pasar como parametro
        /*
        val firebaseUser = firebaseAuth.currentUser




        if(firebaseUser != null){

            //user not null,user is logged in, get user info
            val uid = firebaseUser.uid
            //set to text view
            userName = uid.toString()


        }
        */

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var userName = "IGuowIU1wuPt6SjUiKDfXpFXk6K2"

        db.collection("userAchievements").whereEqualTo("user",userName).get()
        .addOnSuccessListener { documents ->
            for (document in documents){

                db.collection("achievements").document(document.data["achievement"].toString()).get()
                    .addOnSuccessListener { newDocuments ->

                        var title = newDocuments.data?.get("title")?.toString()
                        var url = newDocuments.data?.get("iconActive")?.toString()
                        var description = newDocuments.data?.get("description")?.toString()




                        logrosList += Logro(title.toString(),description.toString())
                        Log.w("tag", "${document.id} => ${logrosList.get(0)} ")
                        Log.w("tag", "${document.id} => ${newDocuments.data?.get("title")?.toString()} ")
                    }

                    .addOnFailureListener { exception->
                        Log.w("tag", "Error getting documents " , exception)
                    }







            }
        }
        .addOnFailureListener { exception->
            Log.w("tag", "Error getting documents " , exception)

        }

        return logrosList

    }
}