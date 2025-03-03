package com.example.pkart.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pkart.activity.LoginActivity
import com.example.pkart.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore  // Firestore instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()  // Initialize Firestore

        binding.registerButton.setOnClickListener {
            val mobile = binding.mobileInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val name = binding.nameInput.text.toString().trim()

            if (mobile.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val email = "$mobile@pkart.com"

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                        // ✅ Store user data in Firestore
                        val userData = hashMapOf(
                            "name" to name,
                            "phone" to mobile
                        )

                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                                auth.signOut()  // Sign out user after registration
                                startActivity(Intent(this, LoginActivity::class.java))  // Redirect to Login
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
    }
}
