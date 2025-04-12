package com.example.pkart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.example.pkart.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrdersFragment : Fragment() {

    private lateinit var orderContainer: LinearLayout
    private lateinit var tvAdminMessage: TextView
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        orderContainer = view.findViewById(R.id.orderContainer)
        tvAdminMessage = view.findViewById(R.id.tvAdminMessage)

        fetchOrders()

        return view
    }

    private fun fetchOrders() {
        if (userId == null) {
            Toast.makeText(requireContext(), "Please log in to view orders", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId)
            .collection("payment")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val emptyTextView = TextView(requireContext()).apply {
                        text = "No orders found"
                        textSize = 16f
                        setPadding(16)
                    }
                    orderContainer.addView(emptyTextView)
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val name = document.getString("name") ?: "Unknown"
                    val phone = document.getString("phone") ?: "Unknown"
                    val address = document.getString("address") ?: "Unknown"
                    val amount = document.getLong("amountToPay") ?: 0
                    val products = document.get("products") as? List<String> ?: listOf("No products")

                    val adminMessage = document.getString("adminMessage") ?: "Order placed, waiting for admin approval"
                    tvAdminMessage.text = "Admin Updates: $adminMessage"

                    val orderTextView = TextView(requireContext()).apply {
                        text = """
                            Name: $name
                            Phone: $phone
                            Address: $address
                            Amount to Pay: ₹$amount
                            Ordered Products: ${products.joinToString(", ")}
                        """.trimIndent()
                        textSize = 16f
                        setPadding(16)
                    }

                    orderContainer.addView(orderTextView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch orders", Toast.LENGTH_SHORT).show()
            }
    }
}
