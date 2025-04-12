package com.example.pkart.adapter

import android.content.Context  // ✅ Correct import
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pkart.activity.ProductDetailActivity
import com.example.pkart.databinding.ItemCategoryProductLayoutBinding
import com.example.pkart.model.AddProductModel

class CategoryProductAdapter(private val context: Context, private val list: ArrayList<AddProductModel>) :
    RecyclerView.Adapter<CategoryProductAdapter.CategoryProductViewHolder>() {

    inner class CategoryProductViewHolder(val binding: ItemCategoryProductLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        val binding = ItemCategoryProductLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryProductViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        Glide.with(context).load(list[position].productCoverImg).into(holder.binding.imageView2)

        holder.binding.textView2.text = list[position].productName

        // ✅ Ensure productSp is converted to a String (if needed)
        holder.binding.textView3.text = list[position].productSp.toString()

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("id", list[position].productId)
            context.startActivity(intent)
        }
    }
}