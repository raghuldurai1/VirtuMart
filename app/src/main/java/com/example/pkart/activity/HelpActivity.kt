package com.example.pkart.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pkart.databinding.ActivityHelpBinding

class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

     //   binding.textView.text = "How to solve your problem"
    }
}
