package com.teenagemoney.app

import com.teenagemoney.app.data.TeenageMoneyRepository
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
    }

    @Test
    fun displayMoney_masksWhenRequested() {
        assertEquals("₹ ••••", TeenageMoneyRepository.displayMoney(125000.0, true))
        val visible = TeenageMoneyRepository.displayMoney(125000.0, false)
        assertTrue(visible.contains("₹"))
        assertTrue(visible.contains("1,25,000") || visible.contains("125,000") || visible.contains("125000"))
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
}
