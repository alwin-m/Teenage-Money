package com.teenagemoney.app.data

data class UserProfile(
    val name: String,
    val dateOfBirth: String,
    val age: Int,
    val status: String,
    val onboardingCompleted: Boolean
)

data class TransactionItem(
    val id: Long,
    val type: String,       // "expense" or "income"
    val title: String,
    val category: String,
    val source: String,
    val amount: Double,
    val day: String,
    val icon: String,
    val color: String,
    val timestamp: String,
    val receiptNumber: String = "",
    val remaining: Double = 0.0,
    val epochMs: Long = 0L  // Device epoch millis for correct week/day calculations
)

data class MoneySpace(
    val name: String,
    val note: String,
    val balance: Double,
    val tint: String,
    val icon: String
)

data class SavingsGoal(
    val id: Long,
    val title: String,
    val target: Double,
    val saved: Double,
    val tint: String,
    val emoji: String
)
