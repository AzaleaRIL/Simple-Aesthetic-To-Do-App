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
import com.finalproj.dobedo.databinding.FragmentSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment() {

    // Initialize Firebase authentication, NavController, and view binding
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignupBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignupBinding.inflate(inflater, container, false)
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

        // Navigate to the login fragment when the user clicks on the "Login" button
        binding.loginRedirect.setOnClickListener {
            navController.navigate(R.id.action_signupFragment_to_loginFragment)
        }

        // Attempt to create a new user when the "Next" button is clicked
        binding.btnNext.setOnClickListener {
            val email = binding.enterMail.text.toString()
            val pass = binding.enterPass.text.toString()
            val repass = binding.enterPass2.text.toString()

            // Check if email, password, and password confirmation fields are not empty
            if (email.isNotEmpty() && pass.isNotEmpty() && repass.isNotEmpty()) {
                // Check if the entered password and password confirmation match
                if (pass == repass) {
                    // Attempt to create a new user using Firebase authentication
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(
                        OnCompleteListener {
                            if (it.isSuccessful) {
                                // Display a success message and navigate to the home fragment
                                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
                                navController.navigate(R.id.action_signupFragment_to_homeFragment)
                            } else {
                                // Display an error message if user registration is not successful
                                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                            }
                        })
                } else {
                    // Display an error message if passwords don't match
                    Toast.makeText(context, "Password doesn't match!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Display an error message if any of the required fields are empty
                Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
