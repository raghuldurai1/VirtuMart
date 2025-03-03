package com.example.pkart.activity

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pkart.databinding.ActivityPrivacyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrivacyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacyBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDeleteAccount.setOnClickListener {
            confirmDeleteAccount()
        }
    }

    private fun confirmDeleteAccount() {
        AlertDialog.Builder(this)
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ ->
                deleteAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        user?.let { firebaseUser ->
            val userId = firebaseUser.uid


            db.collection("Users").document(userId).delete().addOnSuccessListener {

                // Now delete the user from Firebase Authentication
                firebaseUser.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.signOut()
                        finish()
                    } else {
                        // Handle failure case
                    }
                }
            }.addOnFailureListener {
               Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
