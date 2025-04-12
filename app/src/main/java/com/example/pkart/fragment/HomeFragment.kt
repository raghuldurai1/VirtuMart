package com.example.pkart.fragment

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.pkart.R
import com.example.pkart.adapter.ProductAdapter
import com.example.pkart.adapter.CategoryAdapter
import com.example.pkart.databinding.FragmentHomeBinding
import com.example.pkart.model.AddProductModel
import com.example.pkart.model.CategoryModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val categoryList = ArrayList<CategoryModel>()
    private lateinit var categoryAdapter: CategoryAdapter

    private val productList = ArrayList<AddProductModel>()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Setup RecyclerViews
        setupCategoryRecyclerView()
        setupProductRecyclerView()

        val preference = requireContext().getSharedPreferences("info", MODE_PRIVATE)
        if (preference.getBoolean("isCart", false)) {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }

        val editor = preference.edit()
        editor.putBoolean("isCart", false)
        editor.apply()

        getCategories()
        getSliderVideo()
        getProducts()

        return binding.root
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter(requireContext(), categoryList)
        binding.categoryRecycler.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.categoryRecycler.adapter = categoryAdapter
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(productList, requireContext())
        binding.productRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.productRecycler.adapter = productAdapter
    }

    private fun getSliderVideo() {
        val videoPath = "android.resource://" + requireContext().packageName + "/" + R.raw.aboutus
        val uri = Uri.parse(videoPath)

        binding.sliderVideo.setVideoURI(uri)

        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.sliderVideo)
        binding.sliderVideo.setMediaController(mediaController)

        binding.sliderVideo.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            mediaPlayer.setVolume(0f, 0f) // Mutes the audio
            binding.sliderVideo.start()
        }

        binding.sliderVideo.setOnErrorListener { _, _, _ ->
            Toast.makeText(requireContext(), "Error playing video", Toast.LENGTH_SHORT).show()
            false
        }
    }


    private fun getProducts() {
        Firebase.firestore.collection("products").get()
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
                Toast.makeText(requireContext(), "Error fetching products", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCategories() {
        Firebase.firestore.collection("categories").get()
            .addOnSuccessListener { snapshot ->
                categoryList.clear()
                for (doc in snapshot.documents) {
                    val data = doc.toObject(CategoryModel::class.java)
                    if (data != null) {
                        categoryList.add(data)
                    }
                }
                categoryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error fetching categories", Toast.LENGTH_SHORT).show()
            }
    }
}
