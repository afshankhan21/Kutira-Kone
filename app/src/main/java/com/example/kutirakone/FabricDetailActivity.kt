package com.example.kutirakone

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class FabricDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fabric_detail)

        val fabric = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("FABRIC_DATA", Fabric::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getSerializableExtra("FABRIC_DATA") as? Fabric
            }
        } catch (e: Exception) {
            Log.e("FabricDetail", "Error parsing fabric data", e)
            null
        }

        if (fabric == null) {
            Toast.makeText(this, "Error: Fabric data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val nameView: TextView = findViewById(R.id.detailName)
        val priceView: TextView = findViewById(R.id.detailPrice)
        val descriptionView: TextView = findViewById(R.id.detailDescription)
        val imageView: ImageView = findViewById(R.id.detailImage)
        val materialView: TextView = findViewById(R.id.detailMaterial)
        val sizeView: TextView = findViewById(R.id.detailSize)
        val colorView: TextView = findViewById(R.id.detailColor)
        val locationView: TextView = findViewById(R.id.detailLocation)
        val designIdeasView: TextView = findViewById(R.id.designIdeasText)

        val btnBuyNow: Button = findViewById(R.id.contactSellerBtn)
        val btnRequestSwap: Button = findViewById(R.id.swapBtn)
        val btnDelete: Button = findViewById(R.id.deleteListingBtn)

        nameView.text = fabric.title
        
        val priceStr = fabric.price
        priceView.text = if (priceStr.any { it.isDigit() } && !priceStr.startsWith("₹") && !priceStr.contains(Regex("[a-zA-Z]"))) {
            "₹$priceStr"
        } else {
            priceStr
        }

        descriptionView.text = if (fabric.description.isNotEmpty()) fabric.description else "No description provided."
        materialView.text = fabric.materialType
        sizeView.text = fabric.size
        colorView.text = fabric.color
        locationView.text = fabric.location
        
        designIdeasView.text = getDesignIdeas(fabric.materialType)

        // Robust image loading
        if (fabric.imageUrl.isNotEmpty()) {
            if (fabric.imageUrl.startsWith("base64:")) {
                try {
                    val pureBase64 = fabric.imageUrl.substringAfter("base64:")
                    val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    if (bitmap != null) {
                        Glide.with(this)
                            .load(bitmap)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(imageView)
                    } else {
                        imageView.setImageResource(android.R.drawable.ic_menu_report_image)
                    }
                } catch (e: Exception) {
                    imageView.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            } else {
                Glide.with(this)
                    .load(fabric.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(imageView)
            }
        } else {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        btnBuyNow.setOnClickListener {
            Toast.makeText(this, "Buy feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnRequestSwap.setOnClickListener {
            Toast.makeText(this, "Swap request sent to owner!", Toast.LENGTH_SHORT).show()
        }
        
        btnDelete.setOnClickListener {
            deleteFabric(fabric.id)
        }
    }

    private fun getDesignIdeas(material: String): String {
        val mat = material.lowercase()
        return when {
            mat.contains("silk") -> "• Elegant evening clutches\n• Silk hair scrunchies\n• Decorative pillow covers"
            mat.contains("cotton") -> "• Breathable summer tops\n• Patchwork quilts\n• Reusable grocery bags"
            mat.contains("denim") -> "• Tough tote bags\n• Denim coasters\n• Upcycled denim quilts"
            mat.contains("wool") -> "• Cozy winter gloves\n• Woolen coasters\n• Patchwork scarves"
            else -> "• Creative patchwork\n• Small storage pouches\n• Decorative ornaments"
        }
    }

    private fun deleteFabric(id: String) {
        if (id.isEmpty()) return
        FirebaseFirestore.getInstance().collection("fabrics").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Listing deleted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
