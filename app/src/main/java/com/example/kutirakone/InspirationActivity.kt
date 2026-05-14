package com.example.kutirakone

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class InspirationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inspiration)

        recyclerView = findViewById(R.id.inspirationRecyclerView)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Use GridLayout as per the design
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        
        val inspirationList = listOf(
            InspirationItem("Face Masks", "Cotton", "Easy"),
            InspirationItem("Small Pouches", "Silk/Cotton", "Medium"),
            InspirationItem("Patchwork Doll", "Mixed Scraps", "Hard"),
            InspirationItem("Hair Scrunchies", "Silk/Satin", "Easy"),
            InspirationItem("Quilted Coasters", "Cotton Bits", "Medium"),
            InspirationItem("Keychains", "Denim/Leather", "Easy")
        )

        recyclerView.adapter = InspirationAdapter(inspirationList)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.nav_ideas
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_marketplace -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, 0)
                    }
                    finish()
                    true
                }
                R.id.nav_ideas -> true
                R.id.nav_add -> {
                    startActivity(Intent(this, AddFabricActivity::class.java))
                    true
                }
                R.id.nav_nearby -> {
                    startActivity(Intent(this, NearbyActivity::class.java))
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, 0)
                    }
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, 0)
                    }
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bottomNavigationView.selectedItemId = R.id.nav_ideas
    }
}