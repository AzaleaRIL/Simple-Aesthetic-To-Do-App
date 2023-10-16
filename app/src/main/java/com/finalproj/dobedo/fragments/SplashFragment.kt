package com.finalproj.dobedo.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.finalproj.dobedo.R
import com.google.firebase.auth.FirebaseAuth
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView

class SplashFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.dobedosplashcall)

        val logo = view.findViewById<ImageView>(R.id.logo)

        //LOGO Animation
        val bounceAnimation = ScaleAnimation(
            1f, 1.1f,
            1f, 1.1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        bounceAnimation.duration = 2000
        bounceAnimation.repeatCount = Animation.INFINITE
        bounceAnimation.repeatMode = Animation.REVERSE

        logo.startAnimation(bounceAnimation)

        mediaPlayer?.start() // Start playing the background music

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            mediaPlayer?.release() // Release the MediaPlayer
            if (auth.currentUser != null) {
                navController.navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                navController.navigate(R.id.action_splashFragment_to_loginFragment)
            }
        }, 2000)
    }
}
