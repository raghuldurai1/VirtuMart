package com.example.pkart

import android.os.Bundle
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
//import androidx.navigation.ui.setupWithNavController
import com.example.pkart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var i = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup Bottom Navigation
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.bottom_nav)
        binding.bottomBar.setupWithNavController(popupMenu.menu, navController)


        binding.bottomBar.onItemSelected={
            when(it){
                0->{
                    i = 0;
                    navController.navigate(R.id.homeFragment)
                }
                1->i=1
                2->i=2

            }
        }
        // Change Title Based on Destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = when (destination.id) {
                R.id.cartFragment -> "My Cart"
                R.id.moreFragment -> "My Dashboard"
                else -> "P-Kart"
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (i == 0) {
            finish()
        }
}
}
