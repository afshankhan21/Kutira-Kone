package com.example.kutirakone

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Robustly retrieve the Fabric object from Intent
        val fabric = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("FABRIC_DATA", Fabric::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("FABRIC_DATA") as? Fabric
        }

        if (fabric == null) {
            Toast.makeText(this, "Error: Fabric data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize Views from activity_detail.xml
        val nameView: TextView = findViewById(R.id.detailTitle)
        val priceView: TextView = findViewById(R.id.detailPrice)
        val descriptionView: TextView = findViewById(R.id.detailDescription)
        val imageView: ImageView = findViewById(R.id.detailImage)
        val materialView: TextView = findViewById(R.id.detailMaterial)
        val sizeView: TextView = findViewById(R.id.detailSize)
        val colorView: TextView = findViewById(R.id.detailColor)
        val locationView: TextView = findViewById(R.id.detailLocation)
        val designIdeasView: TextView = findViewById(R.id.designIdeasText)

        val btnBuyNow: Button = findViewById(R.id.btnBuyNow)
        val btnRequestSwap: Button = findViewById(R.id.btnRequestSwap)

        // Set Data to Views
        nameView.text = fabric.title
        priceView.text = if (fabric.price.startsWith("₹")) fabric.price else "₹${fabric.price}"
        descriptionView.text = if (fabric.description.isNotEmpty()) fabric.description else "No description provided."
        materialView.text = fabric.materialType
        sizeView.text = fabric.size
        colorView.text = fabric.color
        locationView.text = fabric.location
        
        designIdeasView.text = getDesignIdeas(fabric.materialType)

        Glide.with(this)
            .load(fabric.imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(imageView)

        btnBuyNow.setOnClickListener {
            Toast.makeText(this, "Buy feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnRequestSwap.setOnClickListener {
            Toast.makeText(this, "Swap request sent to owner!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDesignIdeas(material: String): String {
        val mat = material.lowercase()
        return when {
            mat.contains("silk") -> "• Elegant evening clutches\n• Silk hair scrunchies\n• Decorative pillow covers\n• Silk face masks"
            mat.contains("cotton") -> "• Breathable summer tops\n• Patchwork quilts\n• Reusable grocery bags\n• Soft baby clothes"
            mat.contains("denim") -> "• Tough tote bags\n• Denim coasters\n• Pocket organizers\n• Upcycled denim quilts"
            mat.contains("wool") -> "• Cozy winter gloves\n• Woolen coasters\n• Patchwork scarves\n• Small felted toys"
            else -> "• Creative patchwork\n• Small storage pouches\n• Decorative ornaments\n• Gift wrapping accents"
        }
    }
}
