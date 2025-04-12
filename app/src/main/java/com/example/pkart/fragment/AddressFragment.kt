package com.example.pkart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.pkart.databinding.FragmentAddressBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var loadingAnimation: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        loadingAnimation = binding.lottieLoading

        if (userId == null) {
            Toast.makeText(requireContext(), "Please log in first", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        // Load existing user data
        loadUserDetails()

        // Save address on button click
        binding.btnSaveAddress.setOnClickListener {
            saveAddress()
        }

        return binding.root
    }

    private fun loadUserDetails() {
        db.collection("users").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.textName.text = "Name: ${document.getString("name") ?: "N/A"}"
                    binding.textPhone.text = "Phone: ${document.getString("phone") ?: "N/A"}"

                    // Load existing address if available
                    binding.editAddress.setText(document.getString("address") ?: "")
                    binding.editLandmark.setText(document.getString("landmark") ?: "")
                    binding.editState.setText(document.getString("state") ?: "")
                    binding.editCity.setText(document.getString("city") ?: "")
                    binding.editPincode.setText(document.getString("pincode") ?: "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user details", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showLoading() {
        loadingAnimation.visibility = View.VISIBLE
        loadingAnimation.playAnimation()
        binding.btnSaveAddress.isEnabled = false
    }

    private fun hideLoading() {
        loadingAnimation.visibility = View.GONE
        loadingAnimation.cancelAnimation()
        binding.btnSaveAddress.isEnabled = true
    }

    private fun saveAddress() {
        val newAddress = binding.editAddress.text.toString().trim()
        val newLandmark = binding.editLandmark.text.toString().trim()
        val newState = binding.editState.text.toString().trim()
        val newCity = binding.editCity.text.toString().trim()
        val newPincode = binding.editPincode.text.toString().trim()

        if (newAddress.isEmpty() || newLandmark.isEmpty() || newState.isEmpty() || newCity.isEmpty() || newPincode.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading animation
        showLoading()

        val addressMap = mapOf(
            "address" to newAddress,
            "landmark" to newLandmark,
            "state" to newState,
            "city" to newCity,
            "pincode" to newPincode
        )

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .update(addressMap)
                .addOnSuccessListener {
                    hideLoading()
                    Toast.makeText(requireContext(), "Address updated successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Go back to CartFragment
                }
                .addOnFailureListener {
                    hideLoading()
                    Toast.makeText(requireContext(), "Failed to update address", Toast.LENGTH_SHORT).show()
                }
        } else {
            hideLoading()
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}