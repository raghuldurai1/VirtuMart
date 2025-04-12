package com.example.pkart.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pkart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddressActivity : AppCompatActivity() {

    private lateinit var editTextAddress: EditText
    private lateinit var editTextCity: EditText
    private lateinit var editTextState: EditText
    private lateinit var editTextPincode: EditText
    private lateinit var btnSave: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        editTextAddress = findViewById(R.id.editTextAddress)
        editTextCity = findViewById(R.id.editTextCity)
        editTextState = findViewById(R.id.editTextState)
        editTextPincode = findViewById(R.id.editTextPincode)
        btnSave = findViewById(R.id.btnSaveAddress)

        userId = auth.currentUser?.uid

        // Pre-fill fields with user's existing address
        getUserAddress()

        btnSave.setOnClickListener {
            saveUserAddress()
        }
    }

    private fun getUserAddress() {
        if (userId == null) return

        db.collection("users").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    editTextAddress.setText(document.getString("address") ?: "")
                    editTextCity.setText(document.getString("city") ?: "")
                    editTextState.setText(document.getString("state") ?: "")
                    editTextPincode.setText(document.getString("pincode") ?: "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load address", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserAddress() {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val address = editTextAddress.text.toString().trim()
        val city = editTextCity.text.toString().trim()
        val state = editTextState.text.toString().trim()
        val pincode = editTextPincode.text.toString().trim()

        if (address.isEmpty() || city.isEmpty() || state.isEmpty() || pincode.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val addressData = mapOf(
            "address" to address,
            "city" to city,
            "state" to state,
            "pincode" to pincode
        )

        db.collection("users").document(userId!!).update(addressData)
            .addOnSuccessListener {
                Toast.makeText(this, "Address updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Close activity after saving
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update address", Toast.LENGTH_SHORT).show()
            }
    }
}
