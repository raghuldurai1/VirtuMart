package com.example.pkart.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pkart.R
import com.example.pkart.activity.*
import com.example.pkart.databinding.FragmentMoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MoreFragment : Fragment(R.layout.fragment_more) {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private var userAddress: String = ""
    private var userId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        updateUI()
        getUserAddress()

        binding.profileText.setOnClickListener { showPopupMenu() }
        binding.btnCoupons.setOnClickListener { showNoCoupons() }
        binding.btnSavedAddress.setOnClickListener { openSavedAddress() }
        binding.btnPrivacySettings.setOnClickListener { openPrivacySettings() }
        binding.btnTerms.setOnClickListener { openPayments() }
        binding.btnHelpSupport.setOnClickListener { openHelpSupport() }
    }

    private fun updateUI() {
        val user = auth.currentUser
        if (user != null) {
            binding.profileText.text = "Hello, ${user.displayName ?: "User"} ▼"
            userId = user.uid
        } else {
            binding.profileText.text = "Guest ▼"
        }
    }

    private fun getUserAddress() {
        if (userId == null) return

        db.collection("users").document(userId!!).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val address = document.getString("address") ?: ""
                    val city = document.getString("city") ?: ""
                    val state = document.getString("state") ?: ""
                    val pincode = document.getString("pincode") ?: ""

                    if (address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && pincode.isNotEmpty()) {
                        userAddress = "$address, $city, $state - $pincode"
                        binding.textViewAddress.text = "Deliver to: $userAddress"
                        binding.textViewDeliverTo.text = "Deliver to: $userAddress"
                    } else {
                        binding.textViewAddress.text = "No Address Found"
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load address", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.profileText)
        val user = auth.currentUser

        if (user != null) {
            popupMenu.menu.add("Logout").setOnMenuItemClickListener {
                auth.signOut()
                updateUI()
                true
            }
        } else {
            popupMenu.menu.add("Sign Up").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), RegisterActivity::class.java))
                true
            }
            popupMenu.menu.add("Sign In").setOnMenuItemClickListener {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                true
            }
        }
        popupMenu.show()
    }

    private fun showNoCoupons() {
        Toast.makeText(requireContext(), "You don't have any coupons", Toast.LENGTH_SHORT).show()
    }

    private fun openSavedAddress() {
        if (auth.currentUser != null) {
            val intent = Intent(requireContext(), AddressActivity::class.java)
            intent.putExtra("userAddress", userAddress) // Passing the existing address
            startActivity(intent)
        } else {
            promptLogin()
        }
    }

    private fun openPrivacySettings() {
        if (auth.currentUser != null) {
            startActivity(Intent(requireContext(), PrivacyActivity::class.java))
        } else {
            promptLogin()
        }
    }

    private fun openPayments() {
        if (auth.currentUser != null) {
            startActivity(Intent(requireContext(), TermsActivity::class.java))
        } else {
            promptLogin()
        }
    }

    private fun openHelpSupport() {
        startActivity(Intent(requireContext(), HelpActivity::class.java))
    }

    private fun promptLogin() {
        Toast.makeText(requireContext(), "Please log in to access this feature", Toast.LENGTH_SHORT).show()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
    }
}
