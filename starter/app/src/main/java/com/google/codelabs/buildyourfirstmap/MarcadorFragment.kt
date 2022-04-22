package com.google.codelabs.buildyourfirstmap

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.codelabs.buildyourfirstmap.databinding.FragmentMarcadorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_marcador.*
import kotlinx.android.synthetic.main.fragment_marcador.view.*


class MarcadorFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database : DatabaseReference
    private lateinit var _binding: FragmentMarcadorBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //binding.tabLayout.getTabAt(0)!!.text = "10"
        return inflater.inflate(R.layout.fragment_marcador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //_binding = FragmentMarcadorBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMarcadorBinding.bind(view)
        _binding = binding
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        val uId = checkUser()
        checkCurrencyOne(uId)
        checkCurrencyTwo(uId)

        binding.tabLayout.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
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
            activity?.let{
                val intent = Intent (it, LoginActivity::class.java)
                it.startActivity(intent)
            }
            return ""
        }

    }

    private fun checkCurrencyOne(userName: String){

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var currency = ""

        db.collection("users").whereEqualTo("uId",userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                     _binding.tabLayout.getTabAt(0)!!.text = document.data["currencyOne"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }



    }

    private fun checkCurrencyTwo(userName: String){
        //sacamos el id del user autentificado

        //llamamos a la base de datos
        val db = FirebaseFirestore.getInstance()
        //sacamos la colección usuarios para obtener los elementos buscados



        var currency = ""

        db.collection("users").whereEqualTo("uId",userName).get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    _binding.tabLayout.getTabAt(2)!!.text = document.data["currencyTwo"].toString()

                    Log.w("tag", "${document.id} => ${document.data["username"].toString()} ")
                }
            }
            .addOnFailureListener { exception->
                Log.w("tag", "Error getting documents " , exception)

            }



    }


}