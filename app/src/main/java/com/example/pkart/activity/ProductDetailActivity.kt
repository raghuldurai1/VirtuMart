package com.example.pkart.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.pkart.MainActivity
import com.example.pkart.databinding.ActivityProductDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.pkart.R

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var auth: FirebaseAuth  // 🔹 FirebaseAuth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()  // 🔹 Initialize FirebaseAuth

        getProductDetails(intent.getStringExtra("id"))
    }

    private fun getProductDetails(proId: String?) {
        if (proId.isNullOrEmpty()) {
            Toast.makeText(this, "Product ID not found!", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.firestore.collection("products").document(proId).get()
            .addOnSuccessListener { document ->
                val productName = document.getString("productName") ?: "N/A"
                val productSp = document.getString("productSp") ?: "N/A"
                val productDescription = document.getString("productDescription") ?: "No description"
                val productCoverImg = document.getString("productCoverImg")
                val list = document.get("productImages") as? ArrayList<String> ?: arrayListOf()

                binding.textView4.text = productName
                binding.textView5.text = productSp
                binding.textView6.text = productDescription

                val slideList = ArrayList<SlideModel>()
                for (data in list) {
                    slideList.add(SlideModel(data, ScaleTypes.CENTER_CROP))
                }
                binding.imageSlider.setImageList(slideList)

                cartAction(proId, productName, productSp, productCoverImg)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openCart() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun addToCart(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please log in to add items to the cart", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        val cartItem = hashMapOf(
            "productId" to proId,
            "productName" to name,
            "productPrice" to productSp,
            "productCoverImg" to coverImg,
            "quantity" to 1  // Default quantity is 1
        )

        Firebase.firestore.collection("users").document(user.uid)
            .collection("cart").document(proId)
            .set(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
                binding.textView7.text = "Go to Cart"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cartAction(proId: String, name: String?, productSp: String?, coverImg: String?) {
        val user = auth.currentUser
        if (user == null) {
            binding.textView7.text = "Add to Cart"
        } else {
            // Check if the product is already in the user's cart
            Firebase.firestore.collection("users").document(user.uid)
                .collection("cart").document(proId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.textView7.text = "Go to Cart"
                    } else {
                        binding.textView7.text = "Add to Cart"
                    }
                }
        }

        binding.textView7.setOnClickListener {
            if (user == null) {
                Toast.makeText(this, "Please log in to add items to the cart", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                Firebase.firestore.collection("users").document(user.uid)
                    .collection("cart").document(proId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            openCart()
                        } else {
                            addToCart(proId, name, productSp, coverImg)
                        }
                    }
            }
        }
    }
}
