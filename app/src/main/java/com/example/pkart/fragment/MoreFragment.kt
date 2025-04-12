package com.example.pkart.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
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
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var userAddress: String = ""
    private var userId: String? = auth.currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)

        updateUI()
        getUserAddress()

        binding.profileText.setOnClickListener { showPopupMenu() }
        binding.btnCoupons.setOnClickListener { showNoCoupons() }
        binding.btnSavedAddress.setOnClickListener { openSavedAddress() }
        binding.btnPrivacySettings.setOnClickListener { openPrivacySettings() }
        binding.btnTerms.setOnClickListener { openPayments() }
        binding.btnHelpSupport.setOnClickListener { openHelpSupport() }

        // Navigate to OrdersFragment when the button is clicked
        binding.btnYourOrders.setOnClickListener {
            if (auth.currentUser != null) {
                view?.let { Navigation.findNavController(it).navigate(R.id.ordersFragment) }
            } else {
                promptLogin()
            }
        }
    }

    private fun updateUI() {
        val user = auth.currentUser
        binding.profileText.text = if (user != null) {
            "Hello, ${user.displayName ?: "User"} ▼"
        } else {
            "Guest ▼"
        }
    }

    private fun getUserAddress() {
        userId?.let { uid ->
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val address = document.getString("address") ?: ""
                        val city = document.getString("city") ?: ""
                        val state = document.getString("state") ?: ""
                        val pincode = document.getString("pincode") ?: ""

                        userAddress = if (address.isNotEmpty() && city.isNotEmpty() &&
                            state.isNotEmpty() && pincode.isNotEmpty()
                        ) {
                            "$address, $city, $state - $pincode"
                        } else {
                            "No Address Found"
                        }
                       // binding.textViewAddress.text = "Deliver to: $userAddress"
                        binding.textViewDeliverTo.text = "Deliver to: $userAddress"
                    }
                }
                //.addOnFailureListener {
                //    binding.textViewAddress.text = "No Address Found"
                 //   Toast.makeText(requireContext(), "Failed to load address", Toast.LENGTH_SHORT).show()
               // }
        } ?: run {
            //binding.textViewAddress.text = "No Address Found"
        }
    }

    private fun showPopupMenu() {
        val popupMenu = PopupMenu(requireContext(), binding.profileText)
        val user = auth.currentUser

        if (user != null) {
            popupMenu.menu.add("Logout").setOnMenuItemClickListener {
                auth.signOut()
                userId = null
                updateUI()
                getUserAddress()
                Toast.makeText(requireContext(), "Logged out", Toast.LENGTH_SHORT).show()
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
            intent.putExtra("userAddress", userAddress)
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
