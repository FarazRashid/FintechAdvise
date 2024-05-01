package com.se.fintechadvise.AdapterClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.se.fintechadvise.DataClasses.Goal
import com.se.fintechadvise.R

class GoalAdapter(private val goals: List<Goal>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(goal: Goal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_goal, parent, false)
        return GoalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val currentGoal = goals[position]
        holder.bind(currentGoal, listener)
    }

    override fun getItemCount() = goals.size

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val goalTitle: TextView = itemView.findViewById(R.id.goalTitle)
        private val goalType: TextView = itemView.findViewById(R.id.goalType)
        private val goalAmount: TextView = itemView.findViewById(R.id.goalAmount)
        private val goalDate: TextView = itemView.findViewById(R.id.goalDate)
        private val goalPriority : TextView = itemView.findViewById(R.id.goalPriority)

        fun bind(goal: Goal, listener: OnItemClickListener) {
            goalTitle.text = goal.name
            goalType.text = goal.goalType
            goalAmount.text = buildString {
                append("$")
                append(goal.goalAmount)
            }
            goalPriority.text = goal.goalPriority
            goalDate.text = goal.goalDate

            itemView.setOnClickListener {
                listener.onItemClick(goal)
            }
        }
    }
}
