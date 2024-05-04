package com.se.fintechadvise.DataClasses

data class Budget(
    val category: String,
    val color: Int,
    var currentAmount: Double,
    var maxAmount: Double
)