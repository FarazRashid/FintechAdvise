package com.se.fintechadvise.ManagerClasses

import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.DataClasses.InvestmentPerformance

object InvestmentManager {
    private var currentInvestment: Investment? = null
    private var investments: List<Investment> = emptyList()

    fun getCurrentInvestment(): Investment? {
        return currentInvestment
    }

    fun getInvestments(): List<Investment> {
        return investments
    }
    fun setInvestments(setInvestments: List<Investment>) {
        investments = setInvestments
    }

    fun setCurrentInvestment(setinvestment: Investment) {
        currentInvestment = setinvestment
    }

    fun getCurrentInvestmentPerformances(): List<InvestmentPerformance> {
        return currentInvestment?.historicalPerformance ?: emptyList()
    }
    fun resetInvestment() {
        currentInvestment = null
    }

}