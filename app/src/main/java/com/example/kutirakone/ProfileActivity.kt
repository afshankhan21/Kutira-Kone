package com.example.kutirakone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kutirakone.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupButtons()
        setupBottomNavigation()
    }

    private fun setupButtons() {
        binding.btnHistory.setOnClickListener {
            Toast.makeText(this, "Opening History...", Toast.LENGTH_SHORT).show()
        }
        binding.btnSaved.setOnClickListener {
            Toast.makeText(this, "Opening Saved Scraps...", Toast.LENGTH_SHORT).show()
        }
        binding.btnSettings.setOnClickListener {
            Toast.makeText(this, "Opening Settings...", Toast.LENGTH_SHORT).show()
        }
        binding.btnSupport.setOnClickListener {
            Toast.makeText(this, "Opening Help & Support...", Toast.LENGTH_SHORT).show()
        }
        binding.btnLogout.setOnClickListener {
            // Simplified logout logic
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            // In a real app, clear session and navigate to login
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_marketplace -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_ideas -> {
                    startActivity(Intent(this, InspirationActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddFabricActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_nearby -> {
                    startActivity(Intent(this, NearbyActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}