package com.example.kutirakone

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kutirakone.R

class FabricAdapter(
    private var fabrics: List<Fabric>,
    private val onItemClick: (Fabric) -> Unit
) : RecyclerView.Adapter<FabricAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.fabricImage)
        val title: TextView = view.findViewById(R.id.fabricName)
        val material: TextView = view.findViewById(R.id.fabricMaterial)
        val price: TextView = view.findViewById(R.id.fabricPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fabric, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fabric = fabrics[position]
        holder.title.text = fabric.title
        holder.material.text = fabric.materialType
        
        val priceStr = fabric.price
        val displayPrice = if (priceStr.any { it.isDigit() } && !priceStr.startsWith("₹") && !priceStr.contains(Regex("[a-zA-Z]"))) {
            "₹$priceStr"
        } else {
            priceStr
        }
        holder.price.text = displayPrice

        if (fabric.imageUrl.isNotEmpty()) {
            if (fabric.imageUrl.startsWith("base64:")) {
                try {
                    val pureBase64 = fabric.imageUrl.substringAfter("base64:")
                    val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    
                    if (bitmap != null) {
                        Glide.with(holder.itemView.context)
                            .load(bitmap)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .centerCrop()
                            .into(holder.image)
                    } else {
                        holder.image.setImageResource(android.R.drawable.ic_menu_report_image)
                    }
                } catch (e: Exception) {
                    holder.image.setImageResource(android.R.drawable.ic_menu_report_image)
                }
            } else {
                Glide.with(holder.itemView.context)
                    .load(fabric.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .centerCrop()
                    .into(holder.image)
            }
        } else {
            holder.image.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.itemView.setOnClickListener { onItemClick(fabric) }
    }

    override fun getItemCount() = fabrics.size
}
