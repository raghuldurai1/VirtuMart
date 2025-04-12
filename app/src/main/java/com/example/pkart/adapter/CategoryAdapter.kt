package com.example.pkart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pkart.R
import com.example.pkart.activity.CategoryActivity
import com.example.pkart.model.CategoryModel

class CategoryAdapter(private val context: Context, private val categoryList: List<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]

        // Debugging: Print category data
        println("🟢 Binding category: ${category.cate}")

        // Set text safely
        holder.textView.text = category.cate ?: "No Name"

        // Load image safely
        Glide.with(context).load(category.img).into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra("cate", categoryList[position].cate)  // ✅ Fixed
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = categoryList.size

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)
    }
}