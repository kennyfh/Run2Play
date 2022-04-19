package com.google.codelabs.buildyourfirstmap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.codelabs.buildyourfirstmap.databinding.FragmentLogrosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class Logros : Fragment() {

    private var fragmentLogrosBinding: FragmentLogrosBinding? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var _binding : FragmentLogrosBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logros, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLogrosBinding.bind(view)
        _binding = binding
        fragmentLogrosBinding = binding
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        setName(checkUser())




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
        else return ""
    }


    private fun setName(userName: String){
        //sacamos el id del user autentificado
        var userId = checkUser()
        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var name = "marso"

        db.collection("users").whereEqualTo("uId",userId).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    _binding.nombrePerfil.text = document.data["username"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }



    }


    override fun onDestroyView() {

        fragmentLogrosBinding = null
        super.onDestroyView()

    }


}