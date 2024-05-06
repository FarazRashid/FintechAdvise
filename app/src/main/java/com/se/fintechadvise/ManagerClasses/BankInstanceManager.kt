package com.se.fintechadvise.ManagerClasses

object BankInstanceManager {
    private var isConnected : Boolean = false
    fun isBankConnected(): Boolean {
        return isConnected
    }
    fun connectBank() {
        isConnected = true
    }
}