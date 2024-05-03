package com.se.fintechadvise.DataClasses

data class Investment(
    val id: String,
    val name: String,
    var allocation: Double,
    val type: InvestmentType,
    var currentValue: Double,
    val historicalPerformance: List<InvestmentPerformance>,
    val performanceIndex : Double
)