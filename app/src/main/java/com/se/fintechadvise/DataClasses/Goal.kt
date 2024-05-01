package com.se.fintechadvise.DataClasses

data class Goal(
    val name: String,
    val target: String,
    val goalDate: String,
    val currentAmount: String,
    val goalAmount: String,
    val goalType: String,
    val goalId: String,
    val goalPriority : String
)
