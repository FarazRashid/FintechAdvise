package com.se.fintechadvise.DataClasses

data class InvestmentPortfolio(
    val id: String,
    var investments: List<Investment>
)