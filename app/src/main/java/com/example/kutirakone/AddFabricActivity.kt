package com.example.kutirakone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.kutirakone.R
import com.example.kutirakone.databinding.ActivityAddFabricBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddFabricActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFabricBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    
    private var selectedImageUri: Uri? = null
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.fabricImageView.setImageURI(uri)
            binding.fabricImageView.visibility = View.VISIBLE
            binding.uploadHintLayout.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFabricBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val materials = listOf("Material Type", "Cotton", "Silk", "Wool", "Polyester", "Linen", "Denim", "Velvet")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, materials)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.materialSpinner.adapter = adapter

        binding.typeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.fabricPriceInput.visibility = if (checkedId == R.id.radioBuy) View.VISIBLE else View.GONE
        }

        binding.selectImageBtn.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.submitFabricBtn.setOnClickListener {
            validateAndSubmit()
        }

        setupNavigation()
    }

    private fun setupNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_add
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_marketplace -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> true
                else -> false
            }
        }
    }

    private fun validateAndSubmit() {
        val name = binding.fabricNameInput.text.toString().trim()
        val material = binding.materialSpinner.selectedItem.toString()

        if (name.isEmpty() || material == "Material Type") {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.submitFabricBtn.isEnabled = false
        binding.submitFabricBtn.text = "Posting..."

        val defaultLat = 12.9716
        val defaultLon = 77.5946

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                uploadImage(location?.latitude ?: defaultLat, location?.longitude ?: defaultLon)
            }.addOnFailureListener { uploadImage(defaultLat, defaultLon) }
        } else {
            uploadImage(defaultLat, defaultLon)
        }
    }

    private fun uploadImage(lat: Double, lon: Double) {
        val uri = selectedImageUri
        if (uri != null) {
            // Use Base64 to bypass Storage service limitations/errors
            val base64Image = uriToBase64(uri)
            if (base64Image.isNotEmpty()) {
                saveToFirestore(base64Image, lat, lon)
            } else {
                Toast.makeText(this, "Image processing failed, saving without image", Toast.LENGTH_SHORT).show()
                saveToFirestore("", lat, lon)
            }
        } else {
            saveToFirestore("", lat, lon)
        }
    }

    private fun uriToBase64(uri: Uri): String {
        return try {
            val options = android.graphics.BitmapFactory.Options()
            options.inJustDecodeBounds = true
            contentResolver.openInputStream(uri)?.use { 
                android.graphics.BitmapFactory.decodeStream(it, null, options)
            }

            // Keep thumbnail small for Firestore (limit 1MB per document)
            var inSampleSize = 1
            val targetWidth = 320
            if (options.outWidth > targetWidth) {
                inSampleSize = options.outWidth / targetWidth
            }

            options.inJustDecodeBounds = false
            options.inSampleSize = inSampleSize
            
            val bitmap = contentResolver.openInputStream(uri)?.use {
                android.graphics.BitmapFactory.decodeStream(it, null, options)
            } ?: return ""

            val outputStream = java.io.ByteArrayOutputStream()
            // 40% quality is sufficient for thumbnails and keeps string size safe
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 40, outputStream)
            val bytes = outputStream.toByteArray()
            // Using NO_WRAP for cleaner storage in database
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("AddFabric", "Base64 conversion failed: ${e.message}")
            ""
        }
    }

    private fun saveToFirestore(imageUrl: String, lat: Double, lon: Double) {
        // Add prefix for reliable identification during loading
        val finalUrl = if (imageUrl.isNotEmpty() && !imageUrl.startsWith("http") && !imageUrl.startsWith("content://")) {
            "base64:$imageUrl"
        } else {
            imageUrl
        }

        val selectedTypeId = binding.typeRadioGroup.checkedRadioButtonId
        val type = findViewById<RadioButton>(selectedTypeId)?.text?.toString() ?: "SWAP"
        val priceValue = if (type == "BUY") binding.fabricPriceInput.text.toString().trim() else type

        val fabric = hashMapOf(
            "name" to binding.fabricNameInput.text.toString().trim(),
            "size" to binding.fabricSizeInput.text.toString().trim(),
            "color" to binding.fabricColorInput.text.toString().trim(),
            "shopName" to binding.shopNameInput.text.toString().trim(),
            "material" to binding.materialSpinner.selectedItem.toString(),
            "type" to type,
            "price" to priceValue,
            "description" to binding.fabricDescriptionInput.text.toString().trim(),
            "imageUrl" to finalUrl,
            "imageUri" to finalUrl,
            "latitude" to lat,
            "longitude" to lon,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("fabrics")
            .add(fabric)
            .addOnSuccessListener {
                Toast.makeText(this, "Listing posted successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                binding.submitFabricBtn.isEnabled = true
                binding.submitFabricBtn.text = "Post Listing"
                Toast.makeText(this, "Firestore Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
