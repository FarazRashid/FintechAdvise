package com.se.fintechadvise.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Plans
import com.se.fintechadvise.R

class PlanAdapter(private val plans: List<Plans>) : RecyclerView.Adapter<PlanAdapter.PlanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_plan, parent, false)
        return PlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = plans[position]
        holder.planName.text = plan.planName
        holder.planReturn.text = plan.planReturnAmount+"% Return"
//        holder.planImage.setImageResource(plan.planImage.toInt())
        when (plan.planName) {
            "Platinum" -> {
                holder.planImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.platinumColor))
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.platinumColorDark))
            }
            "Gold" -> {
                holder.planImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.goldColor))
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.goldColorDark))
            }
            "Silver" -> {
                holder.planImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.silverColor))
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.silverColorDark))
            }
            "Bronze" -> {
                holder.planImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.bronzeColor))
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.bronzeColorDark))
            }
            else -> {
                holder.planImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.white))

                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            }
        }
    }

    override fun getItemCount() = plans.size
    class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val planName: TextView = view.findViewById(R.id.planName)
        val planReturn: TextView = view.findViewById(R.id.planReturn)
        val planImage: ImageView = view.findViewById(R.id.planImage)
        val cardView: CardView = view.findViewById(R.id.goalsCardView)
    }

}

