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
import com.teenagemoney.app.widget.TeenageMoneyWidget

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
            return weekBoundsMs(today.with(DayOfWeek.MONDAY), zone)
        }

        private fun weekBoundsMs(weekStart: LocalDate, zone: ZoneId): Pair<Long, Long> {
            val startMs = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMs = weekStart.plusDays(7).atStartOfDay(zone).toInstant().toEpochMilli()
            return startMs to endMs
        }

        fun calcSpentForWeek(transactions: List<TransactionItem>, weekStart: LocalDate): Double {
            val zone = ZoneId.systemDefault()
            val (startMs, endMs) = weekBoundsMs(weekStart, zone)
            return transactions
                .filter { it.type == "expense" && it.epochMs in startMs until endMs }
                .sumOf { it.amount }
        }

        /**
         * Calculates the "Spent this week" total from a list of transactions.
         * Only counts expense-type transactions whose epochMs falls in the current week.
         */
        fun calcSpentThisWeek(transactions: List<TransactionItem>): Double {
            val today = LocalDate.now(ZoneId.systemDefault())
            return calcSpentForWeek(transactions, today.with(DayOfWeek.MONDAY))
        }

        fun calcIncomeForWeek(transactions: List<TransactionItem>, weekStart: LocalDate): Double {
            val zone = ZoneId.systemDefault()
            val (startMs, endMs) = weekBoundsMs(weekStart, zone)
            return transactions
                .filter { it.type == "income" && it.epochMs in startMs until endMs }
                .sumOf { it.amount }
        }

        fun calcIncomeThisWeek(transactions: List<TransactionItem>): Double {
            val today = LocalDate.now(ZoneId.systemDefault())
            return calcIncomeForWeek(transactions, today.with(DayOfWeek.MONDAY))
        }

        fun calcTotalIncome(transactions: List<TransactionItem>): Double {
            return transactions.filter { it.type == "income" }.sumOf { it.amount }
        }

        fun calcTotalExpenses(transactions: List<TransactionItem>): Double {
            return transactions.filter { it.type == "expense" }.sumOf { it.amount }
        }

        fun evaluateFinancialPosition(spendingBalance: Double, spentThisWeek: Double): FinancialPosition {
            return when {
                spendingBalance <= 0.0 -> FinancialPosition.LOW_FUNDS
                spendingBalance < 500.0 -> FinancialPosition.LOW_FUNDS
                spendingBalance < 1500.0 -> FinancialPosition.CAREFUL
                spentThisWeek > 0.0 && spendingBalance < spentThisWeek * 0.5 -> FinancialPosition.CAREFUL
                spendingBalance >= 5000.0 || (spentThisWeek > 0.0 && spendingBalance >= spentThisWeek * 1.5) -> FinancialPosition.HEALTHY
                else -> FinancialPosition.ON_TRACK
            }
        }

        fun widgetDateText(date: LocalDate = LocalDate.now(ZoneId.systemDefault())): String {
            return date.format(DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH))
        }

        fun currentMonthBoundsMs(): Pair<Long, Long> {
            val zone = ZoneId.systemDefault()
            val first = LocalDate.now(zone).withDayOfMonth(1)
            return monthBoundsMs(first, zone)
        }

        private fun monthBoundsMs(monthStart: LocalDate, zone: ZoneId): Pair<Long, Long> {
            val next = monthStart.plusMonths(1)
            return monthStart.atStartOfDay(zone).toInstant().toEpochMilli() to
                next.atStartOfDay(zone).toInstant().toEpochMilli()
        }

        fun calcSpentForMonth(transactions: List<TransactionItem>, monthStart: LocalDate): Double {
            val (startMs, endMs) = monthBoundsMs(monthStart.withDayOfMonth(1), ZoneId.systemDefault())
            return transactions
                .filter { it.type == "expense" && it.epochMs in startMs until endMs }
                .sumOf { it.amount }
        }

        fun calcSpentThisMonth(transactions: List<TransactionItem>): Double {
            val today = LocalDate.now(ZoneId.systemDefault())
            return calcSpentForMonth(transactions, today.withDayOfMonth(1))
        }

        fun calcMonthDailyTotals(transactions: List<TransactionItem>): List<Double> {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            return calcMonthDailyTotals(transactions, today.withDayOfMonth(1))
        }

        fun calcMonthDailyTotals(transactions: List<TransactionItem>, monthStart: LocalDate): List<Double> {
            val zone = ZoneId.systemDefault()
            val month = monthStart.withDayOfMonth(1)
            val totals = DoubleArray(month.lengthOfMonth())
            transactions.filter { it.type == "expense" && it.epochMs > 0L }.forEach { tx ->
                val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(tx.epochMs), zone).toLocalDate()
                if (date.year == month.year && date.month == month.month) {
                    totals[date.dayOfMonth - 1] += tx.amount
                }
            }
            return totals.toList()
        }

        fun expensesOnMonthDay(transactions: List<TransactionItem>, dayOfMonth: Int): List<TransactionItem> {
            val today = LocalDate.now(ZoneId.systemDefault())
            return expensesOnMonthDay(transactions, today.withDayOfMonth(1), dayOfMonth)
        }

        fun expensesOnMonthDay(transactions: List<TransactionItem>, monthStart: LocalDate, dayOfMonth: Int): List<TransactionItem> {
            val zone = ZoneId.systemDefault()
            val month = monthStart.withDayOfMonth(1)
            val target = month.withDayOfMonth(dayOfMonth.coerceIn(1, month.lengthOfMonth()))
            return transactions.filter { tx ->
                tx.type == "expense" && tx.epochMs > 0L &&
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(tx.epochMs), zone).toLocalDate() == target
            }
        }

        /**
         * Builds a 7-element list of daily expense totals for the current week
         * (index 0 = Monday … index 6 = Sunday) — used for the bar chart.
         */
        fun calcWeekDailyTotals(transactions: List<TransactionItem>): List<Double> {
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            return calcWeekDailyTotals(transactions, today.with(DayOfWeek.MONDAY))
        }

        fun calcWeekDailyTotals(transactions: List<TransactionItem>, weekStart: LocalDate): List<Double> {
            val zone = ZoneId.systemDefault()
            val totals = DoubleArray(7)
            transactions
                .filter { it.type == "expense" }
                .forEach { tx ->
                    if (tx.epochMs > 0L) {
                        val txDate = LocalDateTime
                            .ofInstant(java.time.Instant.ofEpochMilli(tx.epochMs), zone)
                            .toLocalDate()
                        val dayIndex = txDate.dayOfWeek.value - 1 // Mon=0 … Sun=6
                        val transactionWeekStart = txDate.with(DayOfWeek.MONDAY)
                        if (transactionWeekStart == weekStart) {
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
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            return expensesOnWeekDay(transactions, today.with(DayOfWeek.MONDAY), dayIndex)
        }

        fun expensesOnWeekDay(transactions: List<TransactionItem>, weekStart: LocalDate, dayIndex: Int): List<TransactionItem> {
            val (startMs, endMs) = weekBoundsMs(weekStart, ZoneId.systemDefault())
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
    fun updateSpaceBalance(name: String, newBalance: Double) {
        dbHelper.updateSpaceBalance(name, newBalance)
        TeenageMoneyWidget.refreshAll(context)
    }

    fun addSpace(name: String, note: String = "Custom money space"): Boolean {
        require(name.trim().isNotEmpty()) { "Enter a space name" }
        require(getSpaces().none { it.name.equals(name.trim(), ignoreCase = true) }) { "That space already exists" }
        return dbHelper.addSpace(MoneySpace(name.trim(), note, 0.0, "lavender", "＋"))
    }

    fun getMonthlySalary(): Double = dbHelper.getPreference("monthly_salary", "0").toDoubleOrNull() ?: 0.0
    fun saveMonthlySalary(amount: Double) = dbHelper.setPreference("monthly_salary", amount.toString())

    fun getSalaryAllocation(): SalaryAllocation = SalaryAllocation(
        spendingPercent = dbHelper.getPreference("salary_spending_percent", "50").toIntOrNull() ?: 50,
        savingsPercent = dbHelper.getPreference("salary_savings_percent", "30").toIntOrNull() ?: 30,
        emergencyPercent = dbHelper.getPreference("salary_emergency_percent", "20").toIntOrNull() ?: 20
    )

    fun saveSalaryAllocation(allocation: SalaryAllocation) {
        require(allocation.spendingPercent in 0..100) { "Spending percentage must be 0–100" }
        require(allocation.savingsPercent in 0..100) { "Savings percentage must be 0–100" }
        require(allocation.emergencyPercent in 0..100) { "Emergency percentage must be 0–100" }
        require(allocation.totalPercent == 100) { "Percentages must add up to 100" }
        dbHelper.setPreference("salary_spending_percent", allocation.spendingPercent.toString())
        dbHelper.setPreference("salary_savings_percent", allocation.savingsPercent.toString())
        dbHelper.setPreference("salary_emergency_percent", allocation.emergencyPercent.toString())
    }

    fun isSoundEnabled(): Boolean = dbHelper.getPreference("sound_enabled", "true") == "true"
    fun saveSoundEnabled(enabled: Boolean) = dbHelper.setPreference("sound_enabled", enabled.toString())

    fun isPrivacyMode(): Boolean = dbHelper.getPreference("privacy_mode", "false") == "true"
    fun savePrivacyMode(enabled: Boolean) {
        dbHelper.setPreference("privacy_mode", if (enabled) "true" else "false")
        TeenageMoneyWidget.refreshAll(context)
    }

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
        TeenageMoneyWidget.refreshAll(context)
        return Pair(transaction, newSpending)
    }

    fun addIncome(
        title: String,
        amount: Double,
        category: String = "Income",
        destinationSpace: String = "Spending",
        source: String = "Manual"
    ): TransactionItem {
        val spaces  = getSpaces().associate { it.name to it.balance }.toMutableMap()
        val current = spaces[destinationSpace] ?: 0.0
        require(amount > 0) { "Amount must be greater than zero" }

        val updated = current + amount
        dbHelper.updateSpaceBalance(destinationSpace, updated)

        val epochMs = System.currentTimeMillis()
        val now = LocalDateTime.now()
        val timeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
        val timestamp = now.format(timeFormatter)

        val icon = when (category.lowercase(Locale.ENGLISH)) {
            "allowance", "pocket money" -> "🎁"
            "salary", "job" -> "💼"
            "freelance", "side hustle" -> "💻"
            "gift" -> "✨"
            else -> "↓"
        }
        val color = when (category.lowercase(Locale.ENGLISH)) {
            "freelance", "side hustle" -> "blue"
            "gift" -> "peach"
            else -> "mint"
        }

        val transaction = TransactionItem(
            id            = epochMs,
            type          = "income",
            title         = title.trim().ifBlank { "Added to $destinationSpace" },
            category      = category.trim().ifBlank { "Income" },
            source        = source,
            amount        = amount,
            day           = "Today",
            icon          = icon,
            color         = color,
            timestamp     = timestamp,
            receiptNumber = "",
            remaining     = updated,
            epochMs       = epochMs
        )

        dbHelper.addTransaction(transaction)
        TeenageMoneyWidget.refreshAll(context)
        return transaction
    }

    fun addMoney(amount: Double, destinationSpace: String): TransactionItem {
        return addIncome(
            title = "Added to $destinationSpace",
            amount = amount,
            category = "Income",
            destinationSpace = destinationSpace,
            source = "Manual"
        )
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
        TeenageMoneyWidget.refreshAll(context)
        return transaction
    }

    fun exportJson(): String {
        fun quote(value: String): String = "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
        val profile = getUserProfile()
        val spacesJson = getSpaces().joinToString(",") { "{" + "\"name\":${quote(it.name)},\"note\":${quote(it.note)},\"balance\":${it.balance}" + "}" }
        val txJson = getTransactions().joinToString(",") { "{" + "\"id\":${it.id},\"type\":${quote(it.type)},\"title\":${quote(it.title)},\"category\":${quote(it.category)},\"source\":${quote(it.source)},\"amount\":${it.amount},\"timestamp\":${quote(it.timestamp)}" + "}" }
        val goalsJson = getGoals().joinToString(",") { "{" + "\"id\":${it.id},\"title\":${quote(it.title)},\"target\":${it.target},\"saved\":${it.saved}" + "}" }
        return "{" +
            "\"profile\":{" + "\"name\":${quote(profile?.name ?: "")},\"dateOfBirth\":${quote(profile?.dateOfBirth ?: "")},\"avatarUri\":${quote(profile?.avatarUri ?: "")},\"monthlySalary\":${getMonthlySalary()}" + "}," +
            "\"spaces\":[$spacesJson],\"transactions\":[$txJson],\"goals\":[$goalsJson]" +
            "}"
    }

    // Goals
    fun getGoals(): List<SavingsGoal> = dbHelper.getGoals()

}
