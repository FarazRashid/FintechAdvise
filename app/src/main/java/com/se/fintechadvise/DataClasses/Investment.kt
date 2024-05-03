package com.se.fintechadvise.DataClasses


data class Investment(
    val id: String,
    val name: String,
    var allocation: Double =0.0 ,
    val type: InvestmentType,
    val investmentImageUrl: String,
    var currentValue: Double,
    val historicalPerformance: List<InvestmentPerformance> = emptyList(),
    val performanceIndex : Double
)