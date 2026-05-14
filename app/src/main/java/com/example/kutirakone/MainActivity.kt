package com.example.kutirakone

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabricAdapter: FabricAdapter
    private val fabricList = mutableListOf<Fabric>()
    private val filteredList = mutableListOf<Fabric>()
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var searchEditText: EditText
    
    private lateinit var featuredCard: CardView
    private lateinit var featuredImage: ImageView
    private lateinit var featuredTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText = findViewById(R.id.searchEditText)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        featuredCard = findViewById(R.id.featuredCard)
        featuredImage = findViewById(R.id.featuredImage)
        featuredTitle = findViewById(R.id.featuredTitle)

        fabricAdapter = FabricAdapter(filteredList) { fabric ->
            navigateToDetail(fabric)
        }
        recyclerView.adapter = fabricAdapter

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        setupBottomNavigation()

        loadFabrics()
        setupSearch()
    }

    private fun navigateToDetail(fabric: Fabric) {
        val intent = Intent(this, FabricDetailActivity::class.java)
        intent.putExtra("FABRIC_DATA", fabric)
        startActivity(intent)
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filter(text: String) {
        filteredList.clear()
        if (text.isEmpty()) {
            filteredList.addAll(fabricList)
        } else {
            val query = text.lowercase()
            for (item in fabricList) {
                if (item.title.lowercase().contains(query) || 
                    item.materialType.lowercase().contains(query)) {
                    filteredList.add(item)
                }
            }
        }
        fabricAdapter.notifyDataSetChanged()
    }

    private fun loadFabrics() {
        val db = FirebaseFirestore.getInstance()
        db.collection("fabrics")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("MainActivity", "Listen failed", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    fabricList.clear()
                    for (document in snapshots) {
                        try {
                            val finalImagePath = listOfNotNull(
                                document.getString("imageUrl"),
                                document.getString("imageUri"),
                                document.getString("image")
                            ).firstOrNull { it.isNotBlank() && it != "null" } ?: ""
                            
                            val type = document.getString("type") ?: "SWAP"
                            val rawPrice = document.getString("price")
                            val price = if (type == "BUY") {
                                if (rawPrice != null && rawPrice.isNotEmpty() && rawPrice != "BUY") rawPrice else "0"
                            } else {
                                type
                            }
                            
                            val fabric = Fabric(
                                id = document.id,
                                title = document.getString("name") ?: "Unnamed Fabric",
                                materialType = document.getString("material") ?: "Unknown",
                                size = document.getString("size") ?: "N/A",
                                price = price,
                                color = document.getString("color") ?: "N/A",
                                description = document.getString("description") ?: "",
                                imageUrl = finalImagePath,
                                location = "Nearby"
                            )
                            fabricList.add(fabric)
                        } catch (ex: Exception) {
                            Log.e("MainActivity", "Error parsing fabric", ex)
                        }
                    }
                    
                    filteredList.clear()
                    filteredList.addAll(fabricList)
                    fabricAdapter.notifyDataSetChanged()

                    if (fabricList.isNotEmpty()) {
                        val featured = fabricList[0]
                        featuredTitle.text = "Latest: ${featured.title}"
                        updateFeaturedImage(featured.imageUrl)
                        featuredCard.visibility = View.VISIBLE
                        featuredCard.setOnClickListener { navigateToDetail(featured) }
                    } else {
                        featuredCard.visibility = View.GONE
                    }
                }
            }
    }

    private fun updateFeaturedImage(imageUrl: String) {
        if (imageUrl.isEmpty()) {
            featuredImage.setImageResource(android.R.drawable.ic_menu_gallery)
            return
        }

        if (imageUrl.startsWith("base64:")) {
            try {
                val pureBase64 = imageUrl.substringAfter("base64:")
                val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    Glide.with(this)
                        .load(bitmap)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .centerCrop()
                        .into(featuredImage)
                } else {
                    featuredImage.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } catch (e: Exception) {
                featuredImage.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .centerCrop()
                .into(featuredImage)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.nav_marketplace
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_marketplace -> true
                R.id.nav_map, R.id.nav_nearby -> {
                    startActivity(Intent(this, NearbyActivity::class.java))
                    true
                }
                R.id.nav_ideas -> {
                    startActivity(Intent(this, InspirationActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddFabricActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
