package com.se.fintechadvise.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.InvestmentPerformance
import com.se.fintechadvise.R

class InvestmentHistoryAdapter(private val investmentPerformances: List<InvestmentPerformance>) : RecyclerView.Adapter<InvestmentHistoryAdapter.InvestmentHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvestmentHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_investment_history, parent, false)
        return InvestmentHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvestmentHistoryViewHolder, position: Int) {
        val investmentPerformance = investmentPerformances[position]
        holder.date.text = investmentPerformance.date
        holder.value.text = "$${investmentPerformance.value}"
    }

    override fun getItemCount() = investmentPerformances.size

    class InvestmentHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
        val value: TextView = view.findViewById(R.id.value)
    }
}