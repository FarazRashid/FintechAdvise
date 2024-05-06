package com.se.fintechadvise.AdapterClasses

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Badge
import com.se.fintechadvise.R

// Import the OnBadgeClickListener interface
interface OnBadgeClickListener {
    fun onBadgeClick(position: Int)
}
class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var badgeName: TextView = itemView.findViewById<TextView>(R.id.badgeName)
    var cardView: CardView =
        itemView.findViewById<CardView>(R.id.cardView) // Replace with your actual ID
}

class BadgesAdapter(private val badges: List<Badge>) : RecyclerView.Adapter<BadgeViewHolder>() {
    private var selectedPosition = 0
    var onBadgeClickListener: OnBadgeClickListener? = null

    fun setBadgeClickListener(listener: OnBadgeClickListener) {
        onBadgeClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.categorybadge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val badge: Badge = badges[position]

        holder.badgeName.text = badge.name

        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFB23F"))
            holder.badgeName.setTextColor(Color.WHITE)
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#4E2E21"))
            holder.badgeName.setTextColor(Color.WHITE)
        }

        holder.cardView.setOnClickListener { view: View? ->
            onBadgeClickListener?.onBadgeClick(position)

            // Update selected position
            val previousSelected = selectedPosition
            selectedPosition = position

            // Notify item changed to update UI
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int {
        return badges.size
    }
}
