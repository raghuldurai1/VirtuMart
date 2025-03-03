package com.example.pkart.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pkart.activity.ProductDetailActivity
import com.example.pkart.databinding.LayoutProductItemBinding
import com.example.pkart.model.AddProductModel

class ProductAdapter(private var productList: ArrayList<AddProductModel>) :
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
        holder.binding.button2.text = data.productSp

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailActivity::class.java)
            intent.putExtra("id", productList[position].productId)  // ✅ Fixed `list` issue
            holder.itemView.context.startActivity(intent)  // ✅ Fixed `context` issue
        }
    }

    // ✅ Efficiently update the list using DiffUtil
    fun updateList(newList: List<AddProductModel>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(productList, newList))
        productList.clear()
        productList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}

// ✅ DiffUtil for better performance
class ProductDiffCallback(
    private val oldList: List<AddProductModel>,
    private val newList: List<AddProductModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].productId == newList[newItemPosition].productId  // ✅ Fixed productId issue
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
