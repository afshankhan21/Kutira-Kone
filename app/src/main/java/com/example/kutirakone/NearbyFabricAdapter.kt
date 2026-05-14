package com.example.kutirakone

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NearbyFabricAdapter(
    private var items: List<Fabric>,
    private var userLocation: Location? = null,
    private val onItemClick: (Fabric) -> Unit
) : RecyclerView.Adapter<NearbyFabricAdapter.NearbyViewHolder>() {

    class NearbyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nearbyName)
        val subtitle: TextView = view.findViewById(R.id.nearbySubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nearby_fabric, parent, false)
        return NearbyViewHolder(view)
    }

    override fun onBindViewHolder(holder: NearbyViewHolder, position: Int) {
        val fabric = items[position]
        holder.name.text = fabric.title

        // Handling the location logic safely
        val locLabel = if (userLocation != null) {
            "${fabric.materialType} - Near you"
        } else {
            "${fabric.materialType} - ${fabric.location}"
        }

        holder.subtitle.text = locLabel
        holder.itemView.setOnClickListener { onItemClick(fabric) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Fabric>) {
        items = newItems
        notifyDataSetChanged()
    }
}