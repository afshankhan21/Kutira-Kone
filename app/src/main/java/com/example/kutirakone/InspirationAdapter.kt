package com.example.kutirakone

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InspirationAdapter(private val items: List<InspirationItem>) :
    RecyclerView.Adapter<InspirationAdapter.InspirationViewHolder>() {

    class InspirationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.inspirationTitle)
        val material: TextView = view.findViewById(R.id.inspirationMaterial)
        val difficulty: TextView = view.findViewById(R.id.inspirationDifficulty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InspirationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inspiration, parent, false)
        return InspirationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InspirationViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.material.text = "Material: ${item.material}"
        holder.difficulty.text = item.difficulty
        
        // Apply color coding for difficulty levels as seen in the design
        val colorString = when (item.difficulty.lowercase()) {
            "easy" -> "#4CAF50"   // Green
            "medium" -> "#FF9800" // Orange
            "hard" -> "#F44336"   // Red
            else -> "#5D4037"     // Default theme brown
        }
        holder.difficulty.setTextColor(Color.parseColor(colorString))
    }

    override fun getItemCount() = items.size
}
