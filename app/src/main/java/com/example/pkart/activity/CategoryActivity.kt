package com.example.pkart.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pkart.R
import com.example.pkart.adapter.CategoryProductAdapter
import com.example.pkart.model.AddProductModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryActivity : AppCompatActivity() {

    private val productList = ArrayList<AddProductModel>()  // ✅ Declare productList
    private lateinit var productAdapter: CategoryProductAdapter  // ✅ Declare adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Get the category name from intent
        val category = intent.getStringExtra("cate")

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = CategoryProductAdapter(this, productList)
        recyclerView.adapter = productAdapter

        // Load products
        if (category != null) {
            getProducts(category)
        } else {
            Toast.makeText(this, "Category not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProducts(category: String) {
        Firebase.firestore.collection("products").whereEqualTo("productCategory", category)
            .get()
            .addOnSuccessListener { snapshot ->
                productList.clear()
                for (doc in snapshot.documents) {
                    val data = doc.toObject(AddProductModel::class.java)
                    if (data != null) {
                        productList.add(data)
                    }
                }
                productAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching products", Toast.LENGTH_SHORT).show()
            }
    }
}