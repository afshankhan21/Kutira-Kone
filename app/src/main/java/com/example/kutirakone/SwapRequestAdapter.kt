package com.example.kutirakone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SwapRequestAdapter(private val requests: MutableList<SwapRequest>) :
    RecyclerView.Adapter<SwapRequestAdapter.SwapViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class SwapViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.requestTitle)
        val offer: TextView = view.findViewById(R.id.offeredMaterial)
        val status: TextView = view.findViewById(R.id.requestStatus)
        val acceptBtn: Button = view.findViewById(R.id.acceptBtn)
        val declineBtn: Button = view.findViewById(R.id.declineBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwapViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_swap_request, parent, false)
        return SwapViewHolder(view)
    }

    override fun onBindViewHolder(holder: SwapViewHolder, position: Int) {
        val request = requests[position]
        holder.title.text = "Swap for: ${request.targetName}"
        holder.offer.text = "Offered: ${request.offer}"
        holder.status.text = "Status: ${request.status}"

        if (request.status == "Pending") {
            holder.acceptBtn.visibility = View.VISIBLE
            holder.declineBtn.visibility = View.VISIBLE
        } else {
            holder.acceptBtn.visibility = View.GONE
            holder.declineBtn.visibility = View.GONE
        }

        holder.acceptBtn.setOnClickListener {
            updateRequestStatus(position, "Accepted", holder.itemView)
        }

        holder.declineBtn.setOnClickListener {
            updateRequestStatus(position, "Declined", holder.itemView)
        }
    }

    private fun updateRequestStatus(position: Int, newStatus: String, view: View) {
        val request = requests[position]
        db.collection("swap_requests").document(request.id)
            .update("status", newStatus)
            .addOnSuccessListener {
                Toast.makeText(view.context, "Trade $newStatus", Toast.LENGTH_SHORT).show()
                requests[position] = request.copy(status = newStatus)
                notifyItemChanged(position)
            }
            .addOnFailureListener { e ->
                Toast.makeText(view.context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount() = requests.size
}