package com.se.fintechadvise.ManagerClasses

object BankInstanceManager {
    var isConnected : Boolean = false
    fun isBankConnected(): Boolean {
        return isConnected
    }
    fun connectBank() {
        isConnected = true
    }
}