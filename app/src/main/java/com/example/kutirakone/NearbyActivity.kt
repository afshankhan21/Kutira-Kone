package com.example.kutirakone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class NearbyActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NearbyFabricAdapter
    private val allFabrics = mutableListOf<Fabric>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

        recyclerView = findViewById(R.id.nearbyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NearbyFabricAdapter(allFabrics, null) { fabric ->
            val intent = Intent(this, FabricDetailActivity::class.java)
            intent.putExtra("FABRIC_DATA", fabric)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        loadFabricsFromFirestore()
    }

    private fun loadFabricsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("fabrics").get().addOnSuccessListener { result ->
            allFabrics.clear()
            for (doc in result) {
                val imageUrl = doc.getString("imageUrl") ?: doc.getString("imageUri") ?: ""
                
                val item = Fabric(
                    id = doc.id,
                    title = doc.getString("name") ?: "",
                    materialType = doc.getString("material") ?: "",
                    size = doc.getString("size") ?: "Not specified",
                    color = doc.getString("color") ?: "Not specified",
                    description = doc.getString("description") ?: "",
                    imageUrl = imageUrl,
                    price = doc.getString("type") ?: doc.getString("price") ?: "Free",
                    location = "Within 5km"
                )
                allFabrics.add(item)
            }
            adapter.updateData(allFabrics)
        }
    }
}
