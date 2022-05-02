package com.google.codelabs.buildyourfirstmap

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

class logrosProvider {
    companion object {

        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var database : DatabaseReference

        @JvmStatic fun setup(): List<Logro> {
            var logrosList = listOf<Logro>()


            fun checkUser(): String {
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


            val userName = checkUser()
            //llamamos a la base de datos
            val db = FirebaseFirestore.getInstance()
            //sacamos la colección usuarios para obtener los elementos buscados



            var currency = ""

            db.collection("userAchievements").whereEqualTo("user",userName).get()
                .addOnSuccessListener { documents ->
                    for (document in documents){

                        db.collection("achievements").document(document.data["achievement"].toString()).get()
                            .addOnSuccessListener { newDocuments ->

                                var title = newDocuments.data?.get("title")?.toString()
                                var url = newDocuments.data?.get("iconActive")?.toString()
                                var description = newDocuments.data?.get("description")?.toString()




                                logrosList.plus(Logro(title.toString(),description.toString()))
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
}