package com.example.news26.fragments

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.news26.AuthActivity
import com.example.news26.R
import com.example.news26.databinding.FragmentLoginBinding
import com.example.news26.databinding.FragmentSettingBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class Setting : Fragment() {

    //   this approach is used only in fragment and its views have separate life cycle
    private  var _binding: FragmentSettingBinding? = null
    private val binding  get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//         to make radio button with the current theme

        when(AppCompatDelegate.getDefaultNightMode()){
           AppCompatDelegate.MODE_NIGHT_NO -> binding.radioLight.isChecked = true
           AppCompatDelegate.MODE_NIGHT_YES -> binding.radioDark.isChecked = true
        }

        binding.radioGroup.setOnCheckedChangeListener {_, id ->
            when(id){
                binding.radioDark.id -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.radioLight.id -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.deleteAccountBtn.setOnClickListener {
            reAuthenticateAndDeleteAccount()
        }


    }
    private fun reAuthenticateAndDeleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null && user.email != null) {
            // Show password prompt
            val input = EditText(requireContext()).apply {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Re-authentication Required")
                .setMessage("Please enter your password to delete your account.")
                .setView(input)
                .setPositiveButton("Confirm") { _, _ ->
                    val password = input.text.toString()
                    val credential = EmailAuthProvider.getCredential(user.email!!, password)
                    user.reauthenticate(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            deleteAccount() // Now safe to delete
                        } else {
                            Toast.makeText(requireContext(), "Re-authentication failed: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun deleteAccount(){
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful){
                Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }else{
                Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}