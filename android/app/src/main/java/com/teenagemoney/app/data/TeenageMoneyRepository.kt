package com.teenagemoney.app.data

import android.content.Context
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Random
import java.math.RoundingMode

class TeenageMoneyRepository(private val context: Context) {

    private val dbHelper = TeenageMoneyDbHelper.getInstance(context)

    companion object {
        fun formatMoney(amount: Double): String {
            val hasPaise = kotlin.math.abs(amount % 1.0) > 0.0001
            val scale = if (hasPaise) 2 else 0
            val plain = BigDecimal.valueOf(amount)
                .setScale(scale, RoundingMode.HALF_UP)
                .toPlainString()
            val sign = if (plain.startsWith("-")) "-" else ""
            val unsigned = plain.removePrefix("-")
            val parts = unsigned.split('.')
            val integer = parts[0]
            val groupedInteger = if (integer.length <= 3) {
                integer
            } else {
                val prefix = integer.dropLast(3)
                val firstGroupLength = prefix.length % 2
                val groupedPrefix = buildString {
                    append(prefix.take(firstGroupLength))
                    val remainingGroups = prefix.drop(firstGroupLength).chunked(2)
                    if (firstGroupLength > 0 && remainingGroups.isNotEmpty()) append(",")
                    append(remainingGroups.joinToString(","))
                }
                "$groupedPrefix,${integer.takeLast(3)}"
            }
            val fraction = parts.getOrNull(1)?.let { ".${it.padEnd(2, '0')}" } ?: ""
            return "₹$sign$groupedInteger$fraction"
        }

        fun displayMoney(amount: Double, masked: Boolean): String {
            return if (masked) "₹ ••••" else formatMoney(amount)
        }

        fun calculateAge(dobIsoString: String): Int {
            return try {
                val birthDate = LocalDate.parse(dobIsoString, DateTimeFormatter.ISO_LOCAL_DATE)
                val today = LocalDate.now()
                Period.between(birthDate, today).years
            } catch (e: Exception) {
                0
            }
        }

        fun generateReceiptNumber(): String {
            val now = LocalDate.now()
            val yearTwoDigits = now.year % 100
            val monthStr = String.format(Locale.US, "%02d", now.monthValue)
            val dayStr   = String.format(Locale.US, "%02d", now.dayOfMonth)
            val chars  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val random = Random()
            val suffix = (1..4).map { chars[random.nextInt(chars.length)] }.joinToString("")
            return "TM-$yearTwoDigits-$monthStr-$dayStr-$suffix"
        }

        /**
         * Returns the epoch-millisecond boundaries [startMs, endMs) for the current
         * calendar week (Monday–Sunday) in the device's default timezone.
         */
        fun currentWeekBoundsMs(): Pair<Long, Long> {
            val zone  = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val monday = today.with(DayOfWeek.MONDAY)
            val nextMonday = monday.plusDays(7)
            val startMs = monday.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMs   = nextMonday.atStartOfDay(zone).toInstant().toEpochMilli()
            return Pair(startMs, endMs)
        }

        /**
         * Calculates the "Spent this week" total from a list of transactions.
         * Only counts expense-type transactions whose epochMs falls in the current week.
         */
        fun calcSpentThisWeek(transactions: List<TransactionItem>): Double {
            val (startMs, endMs) = currentWeekBoundsMs()
            return transactions
                .filter { it.type == "expense" && it.epochMs in startMs until endMs }
                .sumOf { it.amount }
        }

        fun currentMonthBoundsMs(): Pair<Long, Long> {
            val zone = ZoneId.systemDefault()
            val first = LocalDate.now(zone).withDayOfMonth(1)
            val next = first.plusMonths(1)
            return first.atStartOfDay(zone).toInstant().toEpochMilli() to
                next.atStartOfDay(zone).toInstant().toEpochMilli()
        }

        fun calcSpentThisMonth(transactions: List<TransactionItem>): Double {
            val (startMs, endMs) = currentMonthBoundsMs()
            return transactions
                .filter { it.type == "expense" && it.epochMs in startMs until endMs }
                .sumOf { it.amount }
        }

        fun calcMonthDailyTotals(transactions: List<TransactionItem>): List<Double> {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val totals = DoubleArray(today.lengthOfMonth())
            transactions.filter { it.type == "expense" && it.epochMs > 0L }.forEach { tx ->
                val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(tx.epochMs), zone).toLocalDate()
                if (date.year == today.year && date.month == today.month) {
                    totals[date.dayOfMonth - 1] += tx.amount
                }
            }
            return totals.toList()
        }

        fun expensesOnMonthDay(transactions: List<TransactionItem>, dayOfMonth: Int): List<TransactionItem> {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            return transactions.filter { tx ->
                tx.type == "expense" && tx.epochMs > 0L &&
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(tx.epochMs), zone).toLocalDate() ==
                    today.withDayOfMonth(dayOfMonth)
            }
        }

        /**
         * Builds a 7-element list of daily expense totals for the current week
         * (index 0 = Monday … index 6 = Sunday) — used for the bar chart.
         */
        fun calcWeekDailyTotals(transactions: List<TransactionItem>): List<Double> {
            val zone   = ZoneId.systemDefault()
            val today  = LocalDate.now(zone)
            val monday = today.with(DayOfWeek.MONDAY)
            val totals = DoubleArray(7)
            transactions
                .filter { it.type == "expense" }
                .forEach { tx ->
                    if (tx.epochMs > 0L) {
                        val txDate = LocalDateTime
                            .ofInstant(java.time.Instant.ofEpochMilli(tx.epochMs), zone)
                            .toLocalDate()
                        val dayIndex = txDate.dayOfWeek.value - 1 // Mon=0 … Sun=6
                        val weekStart = txDate.with(DayOfWeek.MONDAY)
                        if (weekStart == monday) {
                            totals[dayIndex] += tx.amount
                        }
                    }
                }
            return totals.toList()
        }

        /**
         * Current-week expense transactions for a weekday index (Mon=0 … Sun=6).
         */
        fun expensesOnWeekDay(transactions: List<TransactionItem>, dayIndex: Int): List<TransactionItem> {
            val (startMs, endMs) = currentWeekBoundsMs()
            val zone = ZoneId.systemDefault()
            return transactions.filter { tx ->
                tx.type == "expense" &&
                    tx.epochMs in startMs until endMs &&
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(tx.epochMs), zone)
                        .dayOfWeek.value - 1 == dayIndex
            }
        }

        /**
         * Returns the human-readable label for today in context of a transaction.
         * e.g. "Today", "Yesterday", or "dd MMM" for older dates.
         */
        fun dayLabel(epochMs: Long): String {
            if (epochMs <= 0L) return "Today"
            val zone    = ZoneId.systemDefault()
            val txDate  = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMs), zone).toLocalDate()
            val today   = LocalDate.now(zone)
            return when (txDate) {
                today            -> "Today"
                today.minusDays(1) -> "Yesterday"
                else             -> txDate.format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH))
            }
        }
    }

    // Profile & Onboarding
    fun getUserProfile(): UserProfile? = dbHelper.getUserProfile()
    fun saveUserProfile(profile: UserProfile) = dbHelper.saveUserProfile(profile)
    fun isOnboardingCompleted(): Boolean =
        dbHelper.getPreference("onboarding_completed", "false") == "true"
    fun setOnboardingCompleted(completed: Boolean) =
        dbHelper.setPreference("onboarding_completed", if (completed) "true" else "false")

    // Spaces
    fun getSpaces(): List<MoneySpace> = dbHelper.getSpaces()
    fun updateSpaceBalance(name: String, newBalance: Double) =
        dbHelper.updateSpaceBalance(name, newBalance)

    fun addSpace(name: String, note: String = "Custom money space"): Boolean {
        require(name.trim().isNotEmpty()) { "Enter a space name" }
        require(getSpaces().none { it.name.equals(name.trim(), ignoreCase = true) }) { "That space already exists" }
        return dbHelper.addSpace(MoneySpace(name.trim(), note, 0.0, "lavender", "＋"))
    }

    fun getMonthlySalary(): Double = dbHelper.getPreference("monthly_salary", "0").toDoubleOrNull() ?: 0.0
    fun saveMonthlySalary(amount: Double) = dbHelper.setPreference("monthly_salary", amount.toString())

    fun isSoundEnabled(): Boolean = dbHelper.getPreference("sound_enabled", "true") == "true"
    fun saveSoundEnabled(enabled: Boolean) = dbHelper.setPreference("sound_enabled", enabled.toString())

    fun isPrivacyMode(): Boolean = dbHelper.getPreference("privacy_mode", "false") == "true"
    fun savePrivacyMode(enabled: Boolean) = dbHelper.setPreference("privacy_mode", if (enabled) "true" else "false")

    fun getCategories(): List<String> {
        val defaults = listOf("Food", "Transport", "Education", "Shopping", "Entertainment")
        val custom = dbHelper.getPreference("custom_categories", "")
            .split('\u001F')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        return (defaults + custom).distinctBy { it.lowercase(Locale.ENGLISH) }
    }

    fun addCategory(name: String): List<String> {
        val clean = name.trim()
        require(clean.length in 2..32) { "Category must be 2–32 characters" }
        require(getCategories().none { it.equals(clean, ignoreCase = true) }) { "That category already exists" }
        val existing = dbHelper.getPreference("custom_categories", "")
        val next = listOf(existing, clean).filter { it.isNotBlank() }.joinToString("\u001F")
        dbHelper.setPreference("custom_categories", next)
        return getCategories()
    }

    // Transactions
    fun getTransactions(): List<TransactionItem> = dbHelper.getTransactions()

    fun addExpense(
        title: String,
        amount: Double,
        category: String,
        source: String = "UPI"
    ): Pair<TransactionItem, Double> {
        val spaces = getSpaces().associate { it.name to it.balance }.toMutableMap()
        val currentSpending = spaces["Spending"] ?: 0.0
        require(amount > 0)            { "Amount must be greater than zero" }
        require(currentSpending >= amount) { "Not enough in Spending. You have ${formatMoney(currentSpending)}." }

        val newSpending = currentSpending - amount
        updateSpaceBalance("Spending", newSpending)

        val epochMs = System.currentTimeMillis()
        val now  = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val timestamp = now.format(timeFormatter)
        val receiptNumber = generateReceiptNumber()

        val icon = when (category) {
            "Food"          -> "☕"
            "Transport"     -> "⚡"
            "Education"     -> "✎"
            "Shopping"      -> "🛍"
            "Entertainment" -> "★"
            else            -> "✦"
        }
        val color = when (category) {
            "Food"      -> "orange"
            "Transport" -> "blue"
            "Education" -> "mint"
            "Shopping"  -> "violet"
            else        -> "peach"
        }

        val transaction = TransactionItem(
            id            = epochMs,
            type          = "expense",
            title         = title.trim(),
            category      = category,
            source        = source,
            amount        = amount,
            day           = "Today",
            icon          = icon,
            color         = color,
            timestamp     = timestamp,
            receiptNumber = receiptNumber,
            remaining     = newSpending,
            epochMs       = epochMs
        )

        dbHelper.addTransaction(transaction)
        return Pair(transaction, newSpending)
    }

    fun addMoney(amount: Double, destinationSpace: String): TransactionItem {
        val spaces  = getSpaces().associate { it.name to it.balance }.toMutableMap()
        val current = spaces[destinationSpace] ?: 0.0
        require(amount > 0) { "Amount must be greater than zero" }

        val updated = current + amount
        updateSpaceBalance(destinationSpace, updated)

        val epochMs = System.currentTimeMillis()
        val now = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val timestamp = now.format(timeFormatter)

        val transaction = TransactionItem(
            id            = epochMs,
            type          = "income",
            title         = "Added to $destinationSpace",
            category      = "Income",
            source        = "Manual",
            amount        = amount,
            day           = "Today",
            icon          = "↓",
            color         = "mint",
            timestamp     = timestamp,
            receiptNumber = "",
            remaining     = updated,
            epochMs       = epochMs
        )

        dbHelper.addTransaction(transaction)
        return transaction
    }

    fun correctSpaceBalance(spaceName: String, newBalance: Double): TransactionItem? {
        require(newBalance >= 0.0) { "Balance cannot be negative" }
        val current = getSpaces().firstOrNull { it.name == spaceName }?.balance ?: error("Space not found")
        val delta = newBalance - current
        if (delta == 0.0) return null
        val epochMs = System.currentTimeMillis()
        val transaction = TransactionItem(
            id = epochMs,
            type = "adjustment",
            title = "Balance correction — $spaceName",
            category = "Balance correction",
            source = "Manual correction",
            amount = kotlin.math.abs(delta),
            day = "Today",
            icon = "↺",
            color = "peach",
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)),
            remaining = newBalance,
            epochMs = epochMs
        )
        updateSpaceBalance(spaceName, newBalance)
        dbHelper.addTransaction(transaction)
        return transaction
    }

    fun exportJson(): String {
        fun quote(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
        val profile = getUserProfile()
        val spacesJson = getSpaces().joinToString(",") { "{" + "\"name\":${quote(it.name)},\"note\":${quote(it.note)},\"balance\":${it.balance}" + "}" }
        val txJson = getTransactions().joinToString(",") { "{" + "\"id\":${it.id},\"type\":${quote(it.type)},\"title\":${quote(it.title)},\"category\":${quote(it.category)},\"source\":${quote(it.source)},\"amount\":${it.amount},\"timestamp\":${quote(it.timestamp)}" + "}" }
        val goalsJson = getGoals().joinToString(",") { "{" + "\"id\":${it.id},\"title\":${quote(it.title)},\"target\":${it.target},\"saved\":${it.saved}" + "}" }
        return "{" +
            "\"profile\":{" + "\"name\":${quote(profile?.name ?: "")},\"dateOfBirth\":${quote(profile?.dateOfBirth ?: "")},\"monthlySalary\":${getMonthlySalary()}" + "}," +
            "\"spaces\":[$spacesJson],\"transactions\":[$txJson],\"goals\":[$goalsJson]" +
            "}"
    }

    // Goals
    fun getGoals(): List<SavingsGoal> = dbHelper.getGoals()

}
