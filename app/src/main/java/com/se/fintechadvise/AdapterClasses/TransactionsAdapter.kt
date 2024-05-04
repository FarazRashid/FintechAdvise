package com.se.fintechadvise.AdapterClasses

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Transaction
import com.se.fintechadvise.R

class TransactionsAdapter(private var transactions: List<Transaction>, private val onItemClickListener: OnItemClickListener?) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_transactions, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Here you can set the data to the views
        // For example:
//        holder.transactionAmountTextView.text = transaction.transactionAmount
        if (transaction.transactionAmount.contains("+")) {
            holder.transactionAmountTextView.text = transaction.transactionAmount
            holder.transactionAmountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.add_album))
        } else if (transaction.transactionAmount.contains("-")) {
            holder.transactionAmountTextView.text = transaction.transactionAmount
            holder.transactionAmountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        }
        holder.transactionNameTextView.text = transaction.name

        when (transaction.transactionCategory) {
            "Income" -> {
                holder.transactionTypeImageView.setImageResource(R.drawable.income)
                holder.transactionTypeTextView.text = transaction.transactionCategory
                holder.transactionTypeTextView.visibility = View.VISIBLE


            }
            "Spending" -> {
                holder.transactionTypeImageView.setImageResource(R.drawable.card)
                holder.transactionTypeTextView.text = transaction.transactionCategory
                holder.transactionTypeTextView.visibility = View.VISIBLE


            }
            "Bills" -> {
                holder.transactionTypeImageView.setImageResource(R.drawable.bills)
                holder.transactionTypeTextView.text = transaction.transactionCategory
                holder.transactionTypeTextView.visibility = View.VISIBLE


            }
            "Savings" -> {
                holder.transactionTypeImageView.setImageResource(R.drawable.money_bag)
                holder.transactionTypeTextView.text = transaction.transactionCategory
                holder.transactionTypeTextView.visibility = View.VISIBLE

            }
            else -> {
                holder.transactionTypeImageView.setImageResource(R.drawable.transaction)
                holder.transactionTypeTextView.visibility = View.GONE
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position, transaction)
        }


    }

    override fun getItemCount() = transactions.size
    fun updateData(newList: List<Transaction>) {
        Log.d("TransactionsAdapter", "updateData: $newList")
        Log.d("TransactionsAdapter", "oldData: $transactions")
        transactions = newList
        Log.d("TransactionsAdapter", "newData: $transactions")
        notifyDataSetChanged()
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int, transaction: Transaction)
    }
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionTypeImageView: ImageView = view.findViewById(R.id.transactionTypeImageView)
        val transactionNameTextView: TextView = view.findViewById(R.id.transactionNameTextView)
        val transactionTypeTextView: TextView = view.findViewById(R.id.transactionTypeTextView)
        val transactionAmountTextView: TextView = view.findViewById(R.id.transactionAmountTextView)
    }
}