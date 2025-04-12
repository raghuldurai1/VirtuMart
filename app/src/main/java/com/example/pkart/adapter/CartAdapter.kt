package com.example.pkart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pkart.activity.ProductDetailActivity
import com.example.pkart.databinding.LayoutCartItemBinding
import com.example.pkart.roomdb.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(val context: Context, val list: List<ProductModel>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: LayoutCartItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = list[position]
        Glide.with(context).load(product.productImage).into(holder.binding.imageView3)
        holder.binding.textView8.text = product.productName
        holder.binding.textView9.text = product.productSp.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }

        // 🔹 Delete item from Firestore instead of RoomDB
        holder.binding.imageView7.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .collection("cart")
                    .document(product.productId)
                    .delete()
                    .addOnSuccessListener {
                        // Optionally, remove the item from the list and refresh UI
                        notifyItemRemoved(position)
                    }
            }
        }
    }
}
