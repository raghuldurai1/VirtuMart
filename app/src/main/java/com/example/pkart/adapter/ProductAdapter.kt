package com.example.pkart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pkart.activity.LoginActivity
import com.example.pkart.activity.ProductDetailActivity
import com.example.pkart.databinding.LayoutProductItemBinding
import com.example.pkart.model.AddProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductAdapter(private var productList: ArrayList<AddProductModel>, private val context: Context) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: LayoutProductItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = LayoutProductItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductViewHolder(binding)
    }

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val data = productList[position]
        Glide.with(holder.binding.root.context).load(data.productCoverImg).into(holder.binding.imageView5)
        holder.binding.textView12.text = data.productName
        holder.binding.textView13.text = data.productCategory
        holder.binding.textView14.text = data.productMrp


        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("id", productList[position].productId)
            holder.itemView.context.startActivity(intent)
        }

        // Button click listener to add product to cart
        holder.binding.button3.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(context, "Please log in to add items to the cart", Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, LoginActivity::class.java))
            } else {
                val db = Firebase.firestore.collection("users").document(user.uid)
                    .collection("cart").document(data.productId ?: "")

                db.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        Toast.makeText(context, "Item already in cart", Toast.LENGTH_SHORT).show()
                    } else {
                        addToCart(user.uid, data)
                    }
                }
            }
        }
    }

    private fun addToCart(userId: String, product: AddProductModel) {
        val cartItem = hashMapOf(
            "productId" to product.productId,
            "name" to product.productName,
            "price" to product.productSp,
            "image" to product.productCoverImg
        )
        Firebase.firestore.collection("users").document(userId)
            .collection("cart").document(product.productId ?: "")
            .set(cartItem)
            .addOnSuccessListener {
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateList(newList: List<AddProductModel>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(productList, newList))
        productList.clear()
        productList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

class ProductDiffCallback(
    private val oldList: List<AddProductModel>,
    private val newList: List<AddProductModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].productId == newList[newItemPosition].productId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
