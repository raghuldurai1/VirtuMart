package com.example.pkart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pkart.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        setContentView(binding.root)

     //   binding.textView45.text = "Terms and Policies"
    }
}
