package com.example.pkart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pkart.R
import com.example.pkart.adapter.CartAdapter
import com.example.pkart.databinding.FragmentCartBinding
import com.example.pkart.roomdb.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartList = ArrayList<ProductModel>()
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private var userAddress: String? = null // To store the user's address
    private var totalCost = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter(requireContext(), cartList)
        binding.cartRecycler.adapter = cartAdapter

        getCartItems()
        getUserAddress()

        // Handle checkout button
        binding.btnCheckOut.setOnClickListener {
            if (cartList.isEmpty()) {
                Toast.makeText(requireContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userAddress.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please add your address first", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_cartFragment_to_addressFragment)
            } else {
                val bundle = Bundle().apply {
                    putInt("totalAmount", totalCost)
                    putStringArrayList("productNames", ArrayList(cartList.map { it.productName }))
                }
                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment, bundle)
            }
        }

        // Handle Edit Address button
        binding.btnEditAddress.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_addressFragment)
        }

        return binding.root
    }

    private fun getCartItems() {
        if (userId == null) {
            Toast.makeText(requireContext(), "Please log in to view your cart", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId)
            .collection("cart")
            .addSnapshotListener { cartSnapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (cartSnapshots != null) {
                    cartList.clear()
                    totalCost = 0

                    for (cartDocument in cartSnapshots.documents) {
                        val productId = cartDocument.getString("productId") ?: continue

                        db.collection("products").document(productId).get()
                            .addOnSuccessListener { productDocument ->
                                if (productDocument.exists()) {
                                    val productSp = productDocument.getString("productSp")?.toIntOrNull() ?: 0
                                    totalCost += productSp

                                    val product = ProductModel(
                                        productId = productId,
                                        productName = productDocument.getString("productName") ?: "Unknown",
                                        productImage = productDocument.getString("productCoverImg") ?: "",
                                        productSp = productSp.toString()
                                    )
                                    cartList.add(product)
                                    updateCartUI()
                                }
                            }
                    }
                }
            }
    }

    private fun updateCartUI() {
        binding.textView21.text = "Total items in cart: ${cartList.size}"
        binding.textView22.text = "Total Cost: ₹$totalCost"
        cartAdapter.notifyDataSetChanged()

        // Disable checkout if cart is empty
        binding.btnCheckOut.isEnabled = cartList.isNotEmpty()
        binding.btnCheckOut.alpha = if (cartList.isNotEmpty()) 1.0f else 0.5f
    }

    private fun getUserAddress() {
        if (userId == null) return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val address = document.getString("address") ?: ""
                    val city = document.getString("city") ?: ""
                    val state = document.getString("state") ?: ""
                    val pincode = document.getString("pincode") ?: ""

                    if (address.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && pincode.isNotEmpty()) {
                        userAddress = "$address, $city, $state - $pincode"
                        binding.textViewAddress.text = "Deliver to: $userAddress"
                        //binding.textViewDeliverTo.text = "Deliver to: $userAddress"
                    } else {
                        binding.textViewAddress.text = "No Address Found"
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load address", Toast.LENGTH_SHORT).show()
            }
    }
}
