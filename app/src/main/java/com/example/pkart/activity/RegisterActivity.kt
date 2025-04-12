package com.example.pkart.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.pkart.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var lottieSuccess: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        lottieLoading = binding.lottieLoading
        lottieSuccess = binding.lottieSuccess

        binding.registerButton.setOnClickListener {
            val mobile = binding.mobileInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val name = binding.nameInput.text.toString().trim()

            if (mobile.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = "$mobile@pkart.com"

            // Show loading animation
            lottieLoading.visibility = View.VISIBLE
            lottieLoading.playAnimation()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                // Hide loading animation
                lottieLoading.cancelAnimation()
                lottieLoading.visibility = View.GONE

                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        val userData = hashMapOf(
                            "name" to name,
                            "phone" to mobile
                        )

                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                // 🎉 Show success animation
                                lottieSuccess.visibility = View.VISIBLE
                                lottieSuccess.playAnimation()

                                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Registration Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        // ✅ Handle "Already have an account? Login" click event
        binding.loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
