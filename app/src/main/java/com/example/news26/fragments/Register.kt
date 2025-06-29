package com.example.news26.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.news26.MainActivity
import com.example.news26.R
import com.example.news26.databinding.FragmentRegisterBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : Fragment() {

//   viewbinding
      private var _binding : FragmentRegisterBinding? =null
      private val  binding get() = _binding!!
    // here 5 views are
    private lateinit var user : TextInputEditText
    private lateinit var email : TextInputEditText
    private lateinit var password : TextInputEditText

    private lateinit var registerBtn : MaterialButton
    private lateinit var loginBtn : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = parentFragmentManager
        loginBtn = binding.login
        loginBtn.setOnClickListener {
            manager.beginTransaction().replace(
                R.id.containerAuth, Login()
            ).commit()
        }

        binding.btnRegister.setOnClickListener {
            createUser()
        }
        // setting the view
        user = binding.user
        email = binding.email
        password = binding.password


    }


    private fun createUser() {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val name = user.text.toString().trim()
        val email = email.text.toString().trim()
        val password = password.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()){
            Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // creating a user

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {

                        val userId = auth.currentUser?.uid.toString()
                        val map = hashMapOf(
                            "userId" to userId,
                            "name" to  name,
                            "email" to email
                        )

                        userId.let{
                            db.collection("users").document(it)
                                .set(map)
                                .addOnSuccessListener {

                                }
                        }
                        Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
//                        got to main activity
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish() //close AuthActivity

                    }else{
                        val errorMessage = task.exception?.localizedMessage
                        Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
            }



    }

    override fun onDestroy() {
        super.onDestroy()
       _binding =null
    }


}


