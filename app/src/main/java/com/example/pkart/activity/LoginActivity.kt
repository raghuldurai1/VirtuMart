package com.example.pkart.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.pkart.MainActivity
import com.example.pkart.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.pkart.R


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()

        // Get references to the Lottie Animation
        val loginAnimation = findViewById<LottieAnimationView>(R.id.loginButtonAnim)

        binding.loginButton.setOnClickListener {
            val mobile = binding.mobileInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()

            if (mobile.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert mobile number to email format
            val email = "$mobile@pkart.com"

            // Show animation and hide button
            binding.loginButton.visibility = View.GONE
            loginAnimation.visibility = View.VISIBLE
            loginAnimation.playAnimation()

            // Authenticate user
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // Hide animation and show button again
                loginAnimation.visibility = View.GONE
                binding.loginButton.visibility = View.VISIBLE

                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
