package com.se.fintechadvise.DataClasses

data class Transaction(
    val transactionId: String,
    val name: String?,
    var transactionCategory: String,
    val transactionAmount: String,
    val transactionDate: String,

)
