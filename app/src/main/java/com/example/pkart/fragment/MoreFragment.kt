package com.example.pkart.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.pkart.activity.LoginActivity
import com.example.pkart.activity.RegisterActivity
import com.example.pkart.databinding.FragmentMoreBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.pkart.R

class MoreFragment : Fragment(R.layout.fragment_more) {

    private lateinit var binding: FragmentMoreBinding
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)
        auth = FirebaseAuth.getInstance()

        updateUI()

        binding.profileText.setOnClickListener {
            showPopupMenu()
        }
    }

    private fun updateUI() {
        val user = auth.currentUser
        if (user != null) {
            binding.profileText.text = "Hello, ${user.displayName ?: "User"} ▼"
        } else {
            binding.profileText.text = "Guest ▼"
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
}
