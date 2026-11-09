package com.teenagemoney.app

import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.SalaryAllocation
import com.teenagemoney.app.data.FinancialPosition
import com.teenagemoney.app.ui.components.code128BModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TeenageMoneyLogicTest {

    @Test
    fun testAgeCalculation_birthdayOccurred() {
        val today = LocalDate.now()
        // Birthday on Jan 1st 2005 (already occurred this year)
        val dob = "2005-01-01"
        val expectedAge = today.year - 2005
        val calculatedAge = TeenageMoneyRepository.calculateAge(dob)
        assertEquals(expectedAge, calculatedAge)
    }

    @Test
    fun testAgeCalculation_birthdayNotOccurredYet() {
        val today = LocalDate.now()
        // Birthday on Dec 31st 2005 (has not occurred if today is before Dec 31)
        if (today.monthValue < 12 || (today.monthValue == 12 && today.dayOfMonth < 31)) {
            val dob = "2005-12-31"
            val expectedAge = today.year - 2005 - 1
            val calculatedAge = TeenageMoneyRepository.calculateAge(dob)
            assertEquals(expectedAge, calculatedAge)
        }
    }

    @Test
    fun testAgeCalculation_leapYear() {
        val dob = "2004-02-29"
        val calculatedAge = TeenageMoneyRepository.calculateAge(dob)
        assertTrue(calculatedAge >= 20)
    }

    @Test
    fun testReceiptNumberFormat() {
        val receipt = TeenageMoneyRepository.generateReceiptNumber()
        assertTrue(receipt.startsWith("TM-"))
        assertEquals(16, receipt.length) // e.g. TM-26-10-06-A1B2
    }

    @Test
    fun testMoneyFormatting() {
        val formatted = TeenageMoneyRepository.formatMoney(4920.0)
        assertTrue(formatted.contains("4,920") || formatted.contains("4920"))
        assertTrue(formatted.contains("₹"))
    }

    @Test
    fun testMoneyFormatting_preservesPaiseAndIndianGrouping() {
        assertTrue(TeenageMoneyRepository.formatMoney(99.50).contains("99.50"))
        assertTrue(TeenageMoneyRepository.formatMoney(1234567.89).contains("12,34,567.89"))
        assertTrue(TeenageMoneyRepository.formatMoney(100000.0).contains("1,00,000"))
        assertTrue(TeenageMoneyRepository.formatMoney(10000000.0).contains("1,00,00,000"))
    }

    @Test
    fun displayMoney_masksWhenRequested() {
        assertEquals("₹ ••••", TeenageMoneyRepository.displayMoney(125000.0, true))
        val visible = TeenageMoneyRepository.displayMoney(125000.0, false)
        assertTrue(visible.contains("₹"))
        assertTrue(visible.contains("1,25,000") || visible.contains("125,000") || visible.contains("125000"))
    }

    @Test
    fun salaryAllocation_requiresExactlyOneHundredPercent() {
        assertEquals(100, SalaryAllocation(50, 30, 20).totalPercent)
        assertTrue(SalaryAllocation(60, 20, 20).totalPercent == 100)
    }

    @Test
    fun receiptBarcode_encodesPrintableReceiptData() {
        val modules = code128BModules("TM-26-11-03-A1B2")
        assertTrue(modules.isNotEmpty())
        assertTrue(modules.first())
        assertTrue(modules.last())
    }

    @Test
    fun expensesOnWeekDay_onlyCurrentWeekAndMatchingDay() {
        val zone = java.time.ZoneId.systemDefault()
        val monday = LocalDate.now(zone).with(java.time.DayOfWeek.MONDAY)
        val tuesdayThisWeek = monday.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000
        val tuesdayLastWeek = monday.minusDays(6).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000
        val wednesdayThisWeek = monday.plusDays(2).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000

        fun tx(id: Long, epochMs: Long, type: String = "expense") = com.teenagemoney.app.data.TransactionItem(
            id = id,
            type = type,
            title = "Item $id",
            category = "Food",
            source = "UPI",
            amount = 100.0,
            day = "Today",
            icon = "☕",
            color = "orange",
            timestamp = "",
            epochMs = epochMs
        )

        val items = listOf(
            tx(1, tuesdayThisWeek),
            tx(2, tuesdayLastWeek),
            tx(3, wednesdayThisWeek),
            tx(4, tuesdayThisWeek, type = "income")
        )
        val tuesday = TeenageMoneyRepository.expensesOnWeekDay(items, 1)
        assertEquals(1, tuesday.size)
        assertEquals(1L, tuesday.first().id)
        assertEquals(0, TeenageMoneyRepository.expensesOnWeekDay(items, 0).size)
    }

    @Test
    fun expensesOnWeekDay_supportsAViewedPreviousWeek() {
        val zone = java.time.ZoneId.systemDefault()
        val viewedWeek = java.time.LocalDate.now(zone)
            .with(java.time.DayOfWeek.MONDAY)
            .minusWeeks(1)
        val viewedWednesday = viewedWeek.plusDays(2).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000
        val item = com.teenagemoney.app.data.TransactionItem(
            id = 20L,
            type = "expense",
            title = "Previous week item",
            category = "Food",
            source = "Manual",
            amount = 75.0,
            day = "",
            icon = "☕",
            color = "orange",
            timestamp = "",
            epochMs = viewedWednesday
        )

        assertEquals(1, TeenageMoneyRepository.expensesOnWeekDay(listOf(item), viewedWeek, 2).size)
        assertEquals(75.0, TeenageMoneyRepository.calcSpentForWeek(listOf(item), viewedWeek), 0.0)
    }

    @Test
    fun expensesOnMonthDay_supportsAViewedPreviousMonth() {
        val zone = java.time.ZoneId.systemDefault()
        val viewedMonth = java.time.LocalDate.now(zone).withDayOfMonth(1).minusMonths(1)
        val viewedDay = viewedMonth.withDayOfMonth(15).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000
        val item = com.teenagemoney.app.data.TransactionItem(
            id = 21L,
            type = "expense",
            title = "Previous month item",
            category = "Transport",
            source = "Manual",
            amount = 125.0,
            day = "",
            icon = "⚡",
            color = "blue",
            timestamp = "",
            epochMs = viewedDay
        )

        assertEquals(1, TeenageMoneyRepository.expensesOnMonthDay(listOf(item), viewedMonth, 15).size)
        assertEquals(125.0, TeenageMoneyRepository.calcSpentForMonth(listOf(item), viewedMonth), 0.0)
    }

    @Test
    fun testIncomeCalculation_weekAndTotals() {
        val zone = java.time.ZoneId.systemDefault()
        val mondayThisWeek = LocalDate.now(zone).with(java.time.DayOfWeek.MONDAY)
        val wednesdayThisWeek = mondayThisWeek.plusDays(2).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000
        val mondayLastWeek = mondayThisWeek.minusWeeks(1).atStartOfDay(zone).toInstant().toEpochMilli() + 3_600_000

        fun item(id: Long, type: String, amount: Double, epochMs: Long) = com.teenagemoney.app.data.TransactionItem(
            id = id,
            type = type,
            title = "Item $id",
            category = if (type == "income") "Allowance" else "Food",
            source = "Widget",
            amount = amount,
            day = "Today",
            icon = "↓",
            color = "mint",
            timestamp = "",
            epochMs = epochMs
        )

        val items = listOf(
            item(1, "income", 500.0, wednesdayThisWeek),
            item(2, "income", 1200.0, mondayLastWeek),
            item(3, "expense", 150.0, wednesdayThisWeek)
        )

        assertEquals(500.0, TeenageMoneyRepository.calcIncomeThisWeek(items), 0.001)
        assertEquals(1700.0, TeenageMoneyRepository.calcTotalIncome(items), 0.001)
        assertEquals(150.0, TeenageMoneyRepository.calcTotalExpenses(items), 0.001)
    }

    @Test
    fun testFinancialPositionEvaluation() {
        assertEquals(
            FinancialPosition.LOW_FUNDS,
            TeenageMoneyRepository.evaluateFinancialPosition(0.0, 100.0)
        )
        assertEquals(
            FinancialPosition.LOW_FUNDS,
            TeenageMoneyRepository.evaluateFinancialPosition(450.0, 100.0)
        )
        assertEquals(
            FinancialPosition.CAREFUL,
            TeenageMoneyRepository.evaluateFinancialPosition(1200.0, 500.0)
        )
        assertEquals(
            FinancialPosition.HEALTHY,
            TeenageMoneyRepository.evaluateFinancialPosition(8000.0, 500.0)
        )
        assertEquals(
            FinancialPosition.ON_TRACK,
            TeenageMoneyRepository.evaluateFinancialPosition(3000.0, 2500.0)
        )
    }

    @Test
    fun testWidgetDateText_isFormattedProperly() {
        val testDate = LocalDate.of(2026, 11, 6)
        val formatted = TeenageMoneyRepository.widgetDateText(testDate)
        assertTrue(formatted.contains("6 Nov") || formatted.contains("Nov"))
    }
}
