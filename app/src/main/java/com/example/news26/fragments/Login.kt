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
import com.example.news26.databinding.FragmentLoginBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Login : Fragment() {
//   this approach is used only in fragment and its views have separate life cycle
    private  var _binding: FragmentLoginBinding? = null
    private val binding  get() = _binding!!


    private lateinit var emailView: TextInputEditText
    private lateinit var passwordView: TextInputEditText
    private lateinit var registerBtn : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val manager = parentFragmentManager
        registerBtn = binding.register

        registerBtn.setOnClickListener {
            manager.beginTransaction()
                .replace(R.id.containerAuth, Register())
                .commit()
        }

        binding.btnLogin.setOnClickListener {
            signInAccount()
        }

        emailView = binding.email
        passwordView = binding.password



    }

    private fun signInAccount(){
        val auth = FirebaseAuth.getInstance()

        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                // Navigate to MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .addOnFailureListener {
                 e ->
                Toast.makeText(requireContext(), "${e.message}", Toast.LENGTH_LONG).show()
                parentFragmentManager.beginTransaction().replace(R.id.containerAuth, Register())
                    .commit()

            }

    }

    override fun onDestroy() {
        super.onDestroy()
       _binding =null
    }

}