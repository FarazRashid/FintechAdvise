package com.se.fintechadvise.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.R
import com.squareup.picasso.Picasso

class InvestmentAdapter(private val investments: List<Investment>, private val listener: OnInvestmentClickListener) : RecyclerView.Adapter<InvestmentAdapter.InvestmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_investments, parent, false)
        return InvestmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvestmentViewHolder, position: Int) {
        val investment = investments[position]
        holder.investmentName.text = investment.name
        holder.investmentValue.text = "$${investment.currentValue}/stock"
        // Use Picasso to load the image from the URL
        Picasso.get().load(investment.investmentImageUrl).into(holder.investmentImage)

        holder.itemView.setOnClickListener { listener.onInvestmentClick(investment) }

    }
    interface OnInvestmentClickListener {
        fun onInvestmentClick(investment: Investment)
    }
    override fun getItemCount() = investments.size

    class InvestmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val investmentImage: ImageView = view.findViewById(R.id.investmentImage)
        val investmentName: TextView = view.findViewById(R.id.investmentName)
        val investmentValue: TextView = view.findViewById(R.id.investmentValue)
    }
}