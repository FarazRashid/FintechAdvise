package com.se.fintechadvise.ManagerClasses

import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.DataClasses.InvestmentPerformance

object InvestmentManager {
    private var currentInvestment: Investment? = null

    fun getCurrentInvestment(): Investment? {
        return currentInvestment
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