package com.se.fintechadvise.ManagerClasses

import com.se.fintechadvise.DataClasses.Investment
import com.se.fintechadvise.DataClasses.InvestmentPerformance
import org.json.JSONArray

object InvestmentManager {
    private var currentInvestment: Investment? = null
    private var investments: List<Investment> = emptyList()
    private var assetAllocation: List<Pair<String,Double>> = emptyList()

    fun getCurrentInvestment(): Investment? {

        return currentInvestment
    }

    fun getAssetAllocation(): List<Pair<String, Double>> {
        return assetAllocation
    }
    fun setAssetAllocation(setAssetAllocation: List<Pair<String, Double>>) {
        assetAllocation = setAssetAllocation
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