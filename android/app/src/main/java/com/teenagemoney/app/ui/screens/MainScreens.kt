package com.teenagemoney.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.SavingsGoal
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.ui.components.LogExpenseButton
import com.teenagemoney.app.ui.components.OneThingGoalCard
import com.teenagemoney.app.ui.components.RecentActivitySection
import com.teenagemoney.app.ui.components.SpacesSection
import com.teenagemoney.app.ui.components.SpendingHeroCard
import com.teenagemoney.app.ui.components.TopHeader
import com.teenagemoney.app.ui.components.TransactionRow
import com.teenagemoney.app.ui.components.WeekRhythmCard
import com.teenagemoney.app.ui.components.SheetInputField
import com.teenagemoney.app.ui.theme.TMBackground
import com.teenagemoney.app.ui.theme.TMDimens
import com.teenagemoney.app.ui.theme.TMCoral
import com.teenagemoney.app.ui.theme.TMInk
import com.teenagemoney.app.ui.theme.TMLavenderBg
import com.teenagemoney.app.ui.theme.TMLavenderBorder
import com.teenagemoney.app.ui.theme.TMLightMuted
import com.teenagemoney.app.ui.theme.TMLine
import com.teenagemoney.app.ui.theme.TMMintBg
import com.teenagemoney.app.ui.theme.TMMintBorder
import com.teenagemoney.app.ui.theme.TMMintText
import com.teenagemoney.app.ui.theme.TMMuted
import com.teenagemoney.app.ui.theme.TMPeachBg
import com.teenagemoney.app.ui.theme.TMPeachBorder
import com.teenagemoney.app.ui.theme.TMPeachText
import com.teenagemoney.app.ui.theme.TMPurple
import com.teenagemoney.app.ui.theme.TMWhite
import java.time.LocalDate

// ---------------------------------------------------------------------------
// HOME SCREEN
// ---------------------------------------------------------------------------

@Composable
fun HomeScreen(
    userName: String,
    spaces: List<MoneySpace>,
    transactions: List<TransactionItem>,
    goals: List<SavingsGoal>,
    onAddMoneyClick: () -> Unit,
    onLogExpenseClick: () -> Unit,
    onSeeAllActivityClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onAddGoalClick: () -> Unit,
    onSpaceClick: (MoneySpace) -> Unit,
    onAddSpaceClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit,
    amountsMasked: Boolean = false,
    onPrivacyToggle: () -> Unit = {}
) {
    val spendingSpace   = spaces.find { it.name == "Spending" }
    val spendingBalance = spendingSpace?.balance ?: 0.0
    val expenseCount    = transactions.count { it.type == "expense" }
    val otherSpaceCount = spaces.count { it.name != "Spending" }

    val spentThisWeek = remember(transactions) {
        TeenageMoneyRepository.calcSpentThisWeek(transactions)
    }
    val dailyTotals = remember(transactions) {
        TeenageMoneyRepository.calcWeekDailyTotals(transactions)
    }
    var selectedDayIndex by remember { mutableIntStateOf(LocalDate.now().dayOfWeek.value - 1) }
    val selectedDayTransactions = remember(transactions, selectedDayIndex) {
        TeenageMoneyRepository.expensesOnWeekDay(transactions, selectedDayIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .verticalScroll(rememberScrollState())
    ) {
        TopHeader(userName = userName)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TMDimens.Space16),
            verticalArrangement = Arrangement.spacedBy(TMDimens.Space16)
        ) {
            SpendingHeroCard(
                spendingBalance = spendingBalance,
                otherSpaceCount = otherSpaceCount,
                expenseCount = expenseCount,
                amountsMasked = amountsMasked,
                onAddClick = onAddMoneyClick,
                onPrivacyToggle = onPrivacyToggle
            )

            LogExpenseButton(onClick = onLogExpenseClick)

            WeekRhythmCard(
                spentThisWeek = spentThisWeek,
                dailyTotals = dailyTotals,
                selectedDayIndex = selectedDayIndex,
                selectedDayTransactions = selectedDayTransactions,
                amountsMasked = amountsMasked,
                onViewAllClick = onSeeAllActivityClick,
                onDayClick = { selectedDayIndex = it }
            )

            RecentActivitySection(
                transactions = transactions,
                amountsMasked = amountsMasked,
                onSeeAllClick = onSeeAllActivityClick,
                onTransactionClick = onTransactionClick
            )

            SpacesSection(
                spaces = spaces,
                amountsMasked = amountsMasked,
                onSpaceClick = onSpaceClick,
                onAddSpaceClick = onAddSpaceClick
            )

            OneThingGoalCard(
                goal = goals.firstOrNull(),
                onCardClick = onGoalsClick,
                onAddGoalClick = onAddGoalClick
            )

            Spacer(modifier = Modifier.height(TMDimens.Space24))
        }
    }
}

// ---------------------------------------------------------------------------
// ACTIVITY SCREEN
// ---------------------------------------------------------------------------

@Composable
fun ActivityScreen(
    transactions: List<TransactionItem>,
    onLogExpenseClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ALL  ACTIVITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = TMLightMuted
                )
                Text(
                    text = "The money trail",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
                Text(
                    text = "A clear view of where your money has been moving.",
                    fontSize = 13.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(TMWhite)
                .border(1.dp, TMLine, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            if (transactions.isEmpty()) {
                Text(
                    text = "Nothing here yet.\nAdd money or log a spend to see your activity.",
                    fontSize = 13.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    transactions.forEach { t ->
                        TransactionRow(
                            transaction = t,
                            onClick = { onTransactionClick(t) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

// ---------------------------------------------------------------------------
// GOALS SCREEN
// ---------------------------------------------------------------------------

@Composable
fun GoalsScreen(
    goals: List<SavingsGoal>,
    onAddGoalClick: () -> Unit,
    onContributeClick: (SavingsGoal) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "YOUR  WHY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = TMLightMuted
                )
                Text(
                    text = "Goals worth saving for",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
                Text(
                    text = "Give your money somewhere meaningful to go.",
                    fontSize = 13.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(TMCoral)
                    .clickable { onAddGoalClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "＋ New",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(TMWhite)
                    .border(1.dp, TMLine, RoundedCornerShape(22.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "No goals yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TMInk)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Tap \"+ New\" to create your first savings goal.", fontSize = 13.sp, color = TMMuted)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                goals.forEach { g ->
                    val pct  = minOf(100, ((g.saved / maxOf(1.0, g.target)) * 100).toInt())
                    val left = maxOf(0.0, g.target - g.saved)

                    val (bg, border) = when (g.tint) {
                        "mint"     -> Pair(TMMintBg,     TMMintBorder)
                        "lavender" -> Pair(TMLavenderBg, TMLavenderBorder)
                        else       -> Pair(TMPeachBg,    TMPeachBorder)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(22.dp))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$pct% COMPLETE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                    color = TMLightMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = g.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TMInk
                                )
                                Text(
                                    text = if (pct >= 100) "Goal reached! 🎉" else "${TeenageMoneyRepository.formatMoney(left)} left to go",
                                    fontSize = 13.sp,
                                    color = TMMuted,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TMWhite)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pct / 100f)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(TMCoral)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = "＋",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TMCoral,
                                modifier = Modifier.clickable { onContributeClick(g) }.padding(8.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(TMWhite),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = g.emoji, fontSize = 24.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

// ---------------------------------------------------------------------------
// INSIGHTS SCREEN — real data, no fake bar charts
// ---------------------------------------------------------------------------

@Composable
fun InsightsScreen(
    transactions: List<TransactionItem>
) {
    val expenses    = transactions.filter { it.type != "income" }
    val totalSpent  = expenses.sumOf { it.amount }
    val expenseCount = expenses.size

    // Category breakdown from real transactions
    val categoryData = remember(transactions) {
        if (expenses.isEmpty()) {
            emptyMap()
        } else {
            val counts = mutableMapOf<String, Double>()
            expenses.forEach { e -> counts[e.category] = (counts[e.category] ?: 0.0) + e.amount }
            counts
        }
    }

    val topCategory = remember(categoryData) {
        categoryData.entries.maxByOrNull { it.value }
    }

    // Real weekly daily totals for the bar chart
    val dailyTotals = remember(transactions) {
        TeenageMoneyRepository.calcWeekDailyTotals(transactions)
    }
    val maxDaily = dailyTotals.maxOrNull()?.takeIf { it > 0.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Text(
                text = "A  LITTLE  CLARITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = TMLightMuted
            )
            Text(
                text = "Money, in perspective",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TMInk
            )
            Text(
                text = "Simple observations from your real activity.",
                fontSize = 13.sp,
                color = TMMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Card 1: Total Spent (real data)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(TMWhite)
                    .border(1.dp, TMLine, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "TOTAL  SPENT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        color = TMLightMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = TeenageMoneyRepository.formatMoney(totalSpent),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TMInk
                    )
                    Text(
                        text = if (expenseCount == 0) "No purchases logged yet"
                               else "Across $expenseCount ${if (expenseCount == 1) "purchase" else "purchases"} logged",
                        fontSize = 13.sp,
                        color = TMMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    // Real weekly bar chart — same data as home screen
                    if (maxDaily != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            val dayLetters = listOf("M","T","W","T","F","S","S")
                            dailyTotals.forEachIndexed { i, total ->
                                val frac = (total / maxDaily).toFloat().coerceIn(0.04f, 1.0f)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .height((frac * 36).dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (total > 0) TMCoral else Color(0xFFEBE8F9))
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("M","T","W","T","F","S","S").forEach { l ->
                                Text(
                                    text = l,
                                    fontSize = 10.sp,
                                    color = TMLightMuted,
                                    modifier = Modifier.weight(1f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // No data — show flat empty bars
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            repeat(7) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFEBE8F9))
                                )
                            }
                        }
                    }
                }
            }

            // Card 2: Top Category (real data)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(TMLavenderBg)
                    .border(1.dp, TMLavenderBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "YOUR  TOP  CATEGORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        color = TMLightMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = topCategory?.key ?: "None yet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMInk
                    )
                    val pct = if (topCategory != null && totalSpent > 0)
                        ((topCategory.value / totalSpent) * 100).toInt() else 0
                    Text(
                        text = if (pct > 0) "It's $pct% of your spending so far."
                               else "Add expenses to see your top category.",
                        fontSize = 13.sp,
                        color = TMMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(TMWhite)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(pct / 100f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(TMPurple)
                        )
                    }
                }
            }

            // Card 3: Logging Habit — real entry count
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp))
                    .background(TMMintBg)
                    .border(1.dp, TMMintBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "A  GOOD  HABIT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp,
                        color = TMLightMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (expenseCount == 0) "0 entries" else "$expenseCount ${if (expenseCount == 1) "entry" else "entries"}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMInk
                    )
                    Text(
                        text = if (expenseCount == 0) "Start logging to build your habit."
                               else "You're building a mindful money habit.",
                        fontSize = 13.sp,
                        color = TMMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    val dots = minOf(expenseCount, 7)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 0 until 7) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (i < dots) TMMintText else Color(0xFFD4EFE3))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
fun ProfileScreen(
    profile: com.teenagemoney.app.data.UserProfile?,
    monthlySalary: Double,
    spaces: List<MoneySpace>,
    transactions: List<TransactionItem>,
    soundEnabled: Boolean,
    amountsMasked: Boolean,
    appVersion: String,
    buildId: String,
    onEditProfile: () -> Unit,
    onPrivacyToggle: (Boolean) -> Unit,
    onSaveSalary: (Double) -> Unit,
    onSoundEnabledChange: (Boolean) -> Unit,
    onExport: () -> Unit
) {
    var salaryText by remember { mutableStateOf(if (monthlySalary > 0) monthlySalary.toInt().toString() else "") }
    val spent = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val available = spaces.sumOf { it.balance }
    val byCategory = transactions.filter { it.type == "expense" }.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
    Column(
        modifier = Modifier.fillMaxSize().background(TMBackground).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TMInk)
        Text("Your space, your way.", fontSize = 13.sp, color = TMMuted, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.height(20.dp))
        ProfileCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val initials = profile?.name
                    ?.trim()
                    ?.split(Regex("\\s+"))
                    ?.filter { it.isNotEmpty() }
                    ?.take(2)
                    ?.joinToString("") { it.first().uppercase() }
                    ?.ifEmpty { "TM" } ?: "TM"
                Box(
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(TMPeachBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(initials, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TMPeachText)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(profile?.name?.ifBlank { "Your name" } ?: "Your name", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TMInk)
                    Text("Stored privately on this device", fontSize = 12.sp, color = TMMuted)
                }
                TextButton(onClick = onEditProfile) { Text("Edit", color = TMInk, fontWeight = FontWeight.Bold) }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(TMWhite).border(1.dp, TMLine, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Column {
                Text("MONTHLY SALARY", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
                Spacer(modifier = Modifier.height(8.dp))
                SheetInputField(value = salaryText, onValueChange = { salaryText = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "₹ 0", keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(TMCoral).clickable { onSaveSalary(salaryText.toDoubleOrNull() ?: 0.0) }.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text("Save salary", color = TMWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(TMWhite).border(1.dp, TMLine, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Column {
                Text("APP PREFERENCES", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Receipt sound & haptics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TMInk)
                        Text("A subtle confirmation after a saved expense", fontSize = 12.sp, color = TMMuted)
                    }
                    Switch(checked = soundEnabled, onCheckedChange = onSoundEnabledChange)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(14.dp)).background(TMLine).clickable { onExport() }.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text("Export my data", color = TMInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        ProfileCard {
            Text("PRIVACY", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hide financial amounts", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TMInk)
                    Text("Mask balances and transaction amounts across the app", fontSize = 12.sp, color = TMMuted)
                }
                Switch(checked = amountsMasked, onCheckedChange = onPrivacyToggle)
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            ProfileMetric("AVAILABLE", TeenageMoneyRepository.formatMoney(available), Modifier.weight(1f))
            ProfileMetric("TOTAL SPENT", TeenageMoneyRepository.formatMoney(spent), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMetric("EXPENSE ENTRIES", transactions.count { it.type == "expense" }.toString(), Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(TMWhite).border(1.dp, TMLine, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Column {
                Text("WHERE IT WENT", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
                Spacer(modifier = Modifier.height(12.dp))
                if (byCategory.isEmpty()) Text("Your category totals will appear after you log expenses.", fontSize = 13.sp, color = TMMuted)
                else byCategory.toList().sortedByDescending { it.second }.forEach { (category, amount) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(category, fontSize = 14.sp, color = TMInk, fontWeight = FontWeight.SemiBold)
                        Text(TeenageMoneyRepository.formatMoney(amount), fontSize = 14.sp, color = TMInk, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(TMWhite).border(1.dp, TMLine, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Column {
                Text("ABOUT THIS APP", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Version $appVersion", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TMInk)
                Text("Build $buildId", fontSize = 12.sp, color = TMMuted)
            }
        }
        Spacer(modifier = Modifier.height(90.dp))
    }
}

@Composable
private fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(TMWhite)
            .border(1.dp, TMLine, RoundedCornerShape(22.dp))
            .padding(20.dp),
        content = content
    )
}

@Composable
private fun ProfileMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(18.dp)).background(TMWhite).border(1.dp, TMLine, RoundedCornerShape(18.dp)).padding(16.dp)) {
        Column {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, color = TMLightMuted)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TMInk)
        }
    }
}
