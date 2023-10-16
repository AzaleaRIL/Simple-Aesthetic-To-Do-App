package com.finalproj.dobedo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.finalproj.dobedo.R
import com.finalproj.dobedo.databinding.FragmentLoginBinding
import com.finalproj.dobedo.databinding.FragmentSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    // Initialize Firebase authentication, NavController, and view binding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize necessary components and register click events
        init(view)
        registerEvents()
    }

    // Initialize NavController and Firebase authentication
    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    // Register click events for buttons and user interactions
    private fun registerEvents() {

        // Navigate to the signup fragment when the user clicks on the "Sign Up" button
        binding.loginRedirect.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signupFragment)
        }

        // Attempt to log in the user when the "Next" button is clicked
        binding.btnNext.setOnClickListener {
            val email = binding.enterMail.text.toString()
            val pass = binding.enterPass.text.toString()

            // Check if email and password fields are not empty
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Attempt to sign in the user using Firebase authentication
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(
                    OnCompleteListener {
                        if (it.isSuccessful) {
                            // Display a success message and navigate to the home fragment
                            Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_loginFragment_to_homeFragment)
                        } else {
                            // Display an error message if login is not successful
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    })
            } else {
                // Display a message if email or password fields are empty
                Toast.makeText(context, "Empty fields not allowed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
