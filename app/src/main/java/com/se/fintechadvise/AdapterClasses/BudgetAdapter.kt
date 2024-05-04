package com.se.fintechadvise.AdapterClasses

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Budget
import com.se.fintechadvise.R

class BudgetAdapter(val budgets: List<Budget>, private val clickListener: BudgetClickListener) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    interface BudgetClickListener {
        fun onBudgetClick(budget: Budget)
    }

    class BudgetViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val categoryTextView: TextView = view.findViewById(R.id.category)
        val amountTextView: TextView = view.findViewById(R.id.amount)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        holder.categoryTextView.text = budget.category
        holder.amountTextView.text = "$${budget.currentAmount}"
        holder.progressBar.progress = ((budget.currentAmount / budget.maxAmount) * 100).toInt()

        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(holder.view.context, budget.color))
        holder.progressBar.progressTintList = colorStateList

        holder.view.setOnClickListener { clickListener.onBudgetClick(budget) }
    }
    override fun getItemCount() = budgets.size
}