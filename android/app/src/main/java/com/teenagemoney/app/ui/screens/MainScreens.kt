package com.teenagemoney.app.ui.screens

import android.os.SystemClock
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.SalaryAllocation
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.ui.components.LogExpenseButton
import com.teenagemoney.app.ui.components.MonthCalendarCard
import com.teenagemoney.app.ui.components.PeriodSwitcher
import com.teenagemoney.app.ui.components.ProfileAvatar
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
import java.time.DayOfWeek
import java.time.LocalDate

// ---------------------------------------------------------------------------
// HOME SCREEN
// ---------------------------------------------------------------------------

@Composable
fun HomeScreen(
    userName: String,
    avatarUri: String = "",
    spaces: List<MoneySpace>,
    transactions: List<TransactionItem>,
    onAddMoneyClick: () -> Unit,
    onLogExpenseClick: () -> Unit,
    onSeeAllActivityClick: () -> Unit,
    onSpaceClick: (MoneySpace) -> Unit,
    onAddSpaceClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit,
    amountsMasked: Boolean = false,
    onPrivacyToggle: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val spendingSpace   = spaces.find { it.name == "Spending" }
    val spendingBalance = spendingSpace?.balance ?: 0.0
    val expenseCount    = transactions.count { it.type == "expense" }
    val otherSpaceCount = spaces.count { it.name != "Spending" }
    val today = LocalDate.now(java.time.ZoneId.systemDefault())
    val currentWeekStart = today.with(DayOfWeek.MONDAY)

    var weekStart by remember(today) { mutableStateOf(currentWeekStart) }
    val currentMonthStart = today.withDayOfMonth(1)
    var monthStart by remember(today) { mutableStateOf(currentMonthStart) }
    val spentThisWeek = remember(transactions, weekStart) {
        TeenageMoneyRepository.calcSpentForWeek(transactions, weekStart)
    }
    val dailyTotals = remember(transactions, weekStart) {
        TeenageMoneyRepository.calcWeekDailyTotals(transactions, weekStart)
    }
    val spentThisMonth = remember(transactions, monthStart) {
        TeenageMoneyRepository.calcSpentForMonth(transactions, monthStart)
    }
    val monthDailyTotals = remember(transactions, monthStart) {
        TeenageMoneyRepository.calcMonthDailyTotals(transactions, monthStart)
    }
    var selectedPeriod by remember { mutableStateOf("Week") }
    var selectedDayIndex by remember(today) { mutableIntStateOf(today.dayOfWeek.value - 1) }
    var selectedMonthDay by remember(today) { mutableIntStateOf(today.dayOfMonth) }
    val selectedDayTransactions = remember(transactions, selectedDayIndex, weekStart) {
        TeenageMoneyRepository.expensesOnWeekDay(transactions, weekStart, selectedDayIndex)
    }
    val selectedMonthDate = monthStart.withDayOfMonth(selectedMonthDay.coerceAtMost(monthStart.lengthOfMonth()))
    val selectedMonthTransactions = remember(transactions, selectedMonthDay, monthStart) {
        TeenageMoneyRepository.expensesOnMonthDay(transactions, monthStart, selectedMonthDay)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .verticalScroll(rememberScrollState())
    ) {
        TopHeader(userName = userName, avatarUri = avatarUri, onProfileClick = onProfileClick)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TMDimens.Space16),
            verticalArrangement = Arrangement.spacedBy(TMDimens.Space16)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedPeriod == "Month") "YOUR MONTH" else "YOUR WEEK",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.1.sp,
                    color = TMMuted
                )
                PeriodSwitcher(
                    selected = selectedPeriod,
                    onSelected = {
                        selectedPeriod = it
                        if (it == "Week") {
                            weekStart = currentWeekStart
                            selectedDayIndex = today.dayOfWeek.value - 1
                        } else {
                            monthStart = currentMonthStart
                            selectedMonthDay = today.dayOfMonth
                        }
                    }
                )
            }

            SpendingHeroCard(
                spendingBalance = spendingBalance,
                otherSpaceCount = otherSpaceCount,
                expenseCount = expenseCount,
                amountsMasked = amountsMasked,
                onAddClick = onAddMoneyClick,
                onPrivacyToggle = onPrivacyToggle
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LogExpenseButton(onClick = onLogExpenseClick)
            }

            if (selectedPeriod == "Month") {
                MonthCalendarCard(
                    monthTotal = spentThisMonth,
                    dailyTotals = monthDailyTotals,
                    selectedDay = selectedMonthDate,
                    selectedDayTransactions = selectedMonthTransactions,
                    amountsMasked = amountsMasked,
                    monthStart = monthStart,
                    onDayClick = { selectedMonthDay = it.dayOfMonth },
                    onMonthChange = { offset ->
                        val nextMonthStart = monthStart.plusMonths(offset.toLong()).withDayOfMonth(1)
                        if (!nextMonthStart.isAfter(currentMonthStart)) {
                            monthStart = nextMonthStart
                            selectedMonthDay = if (nextMonthStart == currentMonthStart) today.dayOfMonth else 1
                        }
                    }
                )
            } else {
                WeekRhythmCard(
                    spentThisWeek = spentThisWeek,
                    dailyTotals = dailyTotals,
                    selectedDayIndex = selectedDayIndex,
                    selectedDayTransactions = selectedDayTransactions,
                    amountsMasked = amountsMasked,
                    onViewAllClick = onSeeAllActivityClick,
                    weekStart = weekStart,
                    onDayClick = { selectedDayIndex = it },
                    onWeekChange = { offset ->
                        val nextWeekStart = weekStart.plusWeeks(offset.toLong())
                        if (!nextWeekStart.isAfter(currentWeekStart)) {
                            weekStart = nextWeekStart
                            if (nextWeekStart == currentWeekStart) selectedDayIndex = today.dayOfWeek.value - 1
                        }
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (selectedPeriod == "Month") "Nice and steady." else "Your week, at a glance.",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    color = TMInk
                )
                Text(
                    text = "${TeenageMoneyRepository.displayMoney(spendingBalance, amountsMasked)} left in Spending",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = (-0.4).sp,
                    color = TMMuted
                )
            }

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

            Spacer(modifier = Modifier.height(TMDimens.Space24))
        }
    }
}

// ---------------------------------------------------------------------------
// ACTIVITY SCREEN
// ---------------------------------------------------------------------------

@Composable
fun ActivityScreen(
    profile: com.teenagemoney.app.data.UserProfile? = null,
    transactions: List<TransactionItem>,
    amountsMasked: Boolean = false,
    onLogExpenseClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit = {}
) {
    val today = LocalDate.now()
    val daysLived = profile?.dateOfBirth?.let {
        runCatching { java.time.temporal.ChronoUnit.DAYS.between(LocalDate.parse(it), today).toInt() }.getOrNull()
    } ?: 0
    val daysAhead = profile?.dateOfBirth?.let {
        runCatching { java.time.temporal.ChronoUnit.DAYS.between(today, LocalDate.parse(it).plusYears(75)).toInt().coerceAtLeast(0) }.getOrNull()
    } ?: 0
    val grouped = transactions.groupBy { TeenageMoneyRepository.dayLabel(it.epochMs) }

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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ACTIVITY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = TMLightMuted
                )
                Text(
                    text = "Your money trail",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
            }
            Button(
                onClick = onLogExpenseClick,
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral, contentColor = TMWhite),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.heightIn(min = TMDimens.MinTouch)
            ) {
                Text("Log expense", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(daysLived.toString(), fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = TMCoral)
                Text("DAYS LIVED", fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, color = TMMuted)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(daysAhead.toString(), fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, color = TMInk)
                Text("DAYS AHEAD", fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, color = TMMuted)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(75) { index ->
                Box(modifier = Modifier.weight(1f).height(5.dp).clip(RoundedCornerShape(3.dp)).background(when {
                    index < (daysLived / 365).coerceIn(0, 75) -> TMInk
                    index == (daysLived / 365).coerceIn(0, 74) -> TMCoral
                    else -> TMLine
                }))
            }
        }
        Text("$daysAhead days ahead. Start today${profile?.name?.let { ", ${it.lowercase()}" } ?: ""}.", fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 12.sp, color = TMMuted, modifier = Modifier.padding(top = 10.dp))

        Spacer(modifier = Modifier.height(28.dp))

        if (transactions.isEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("☕", fontSize = 42.sp)
                Text("nothing here yet", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = TMInk)
                Text("your first expense will show up here", fontSize = 14.sp, color = TMMuted, modifier = Modifier.padding(top = 4.dp))
            }
        } else {
            grouped.forEach { (day, dayTransactions) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(day.uppercase(), fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TMMuted)
                    Text(TeenageMoneyRepository.displayMoney(dayTransactions.filter { it.type == "expense" }.sumOf { it.amount }, amountsMasked), fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 11.sp, color = TMMuted)
                }
                dayTransactions.forEach { t ->
                    TransactionRow(transaction = t, amountsMasked = amountsMasked, onClick = { onTransactionClick(t) })
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
    salaryAllocation: SalaryAllocation = SalaryAllocation(),
    profileAvatarUri: String = "",
    spaces: List<MoneySpace>,
    transactions: List<TransactionItem>,
    soundEnabled: Boolean,
    amountsMasked: Boolean,
    appVersion: String,
    buildId: String,
    onEditProfile: () -> Unit,
    onEditProfilePhoto: () -> Unit = {},
    onPrivacyToggle: (Boolean) -> Unit,
    onSaveSalary: (Double) -> Unit,
    onEditSalaryAllocation: () -> Unit = {},
    onShowCreatorDetails: () -> Unit = {},
    onSoundEnabledChange: (Boolean) -> Unit,
    onExport: () -> Unit,
    onAddMoneyClick: () -> Unit = {},
    onEditSpendingBalance: () -> Unit = {},
    onEditSpaceBalance: (MoneySpace) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var salaryText by remember { mutableStateOf(if (monthlySalary > 0) monthlySalary.toInt().toString() else "") }
    val spent = transactions.filter { it.type == "expense" }.sumOf { it.amount }
    val spendingBalance = spaces.find { it.name == "Spending" }?.balance ?: 0.0
    val otherSpacesBalance = spaces.filter { it.name != "Spending" }.sumOf { it.balance }
    val byCategory = transactions.filter { it.type == "expense" }.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }
    val initials = profile?.name?.trim()?.split(Regex("\\s+"))?.filter { it.isNotEmpty() }?.take(2)?.joinToString("") { it.first().uppercase() }?.ifEmpty { "TM" } ?: "TM"
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFE5DDD0)).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(TMWhite)
                    .clickable(onClick = onBack)
                    .semantics {
                        contentDescription = "Back"
                        role = Role.Button
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("‹", fontSize = 30.sp, color = TMInk)
            }
            Text("Teenage Money Profile", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TMInk)
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(TMCoral), contentAlignment = Alignment.Center) {
                Text(initials.take(1), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TMWhite)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(TMWhite)
                    .clickable { onEditProfile() }
                    .semantics {
                        contentDescription = "Edit profile"
                        role = Role.Button
                    },
                contentAlignment = Alignment.Center
            ) {
                ProfileAvatar(
                    avatarUri = profileAvatarUri,
                    initials = initials,
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Profile photo"
                )
            }
            TextButton(onClick = onEditProfilePhoto) {
                Text(if (profileAvatarUri.isBlank()) "Add profile photo" else "Change profile photo", color = TMInk)
            }
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .heightIn(min = TMDimens.MinTouch)
                    .clickable { onEditProfile() }
                    .semantics {
                        contentDescription = "Edit profile name"
                        role = Role.Button
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(profile?.name?.ifBlank { "Your name" } ?: "Your name", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TMInk)
                Spacer(modifier = Modifier.width(6.dp))
                Text("✎", fontSize = 18.sp, color = TMMuted)
            }
            Text("Stored privately on this device", fontSize = 12.sp, color = TMMuted)
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SPENDING MONEY LEFT", fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 1.2.sp, color = TMMuted)
            Text(TeenageMoneyRepository.displayMoney(spaces.find { it.name == "Spending" }?.balance ?: 0.0, amountsMasked), fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = TMInk)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(TMCoral))
                Spacer(modifier = Modifier.width(6.dp))
                Text("${transactions.count { it.type == "expense" }} expense entries logged", fontSize = 13.sp, color = TMMuted)
            }
        }

        ProfileCard {
            Text("LOCAL MONEY SNAPSHOT", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
            Spacer(modifier = Modifier.height(12.dp))
            SnapshotRow("Spending space", TeenageMoneyRepository.displayMoney(spendingBalance, amountsMasked))
            SnapshotRow("Other spaces", TeenageMoneyRepository.displayMoney(otherSpacesBalance, amountsMasked))
            SnapshotRow("Recorded transactions", transactions.size.toString())
            Spacer(modifier = Modifier.height(8.dp))
            Text("EDIT A MONEY SPACE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, color = TMLightMuted)
            spaces.forEach { space ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(space.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TMInk)
                        Text(TeenageMoneyRepository.displayMoney(space.balance, amountsMasked), fontSize = 12.sp, color = TMMuted)
                    }
                    TextButton(onClick = { onEditSpaceBalance(space) }) {
                        Text("Edit", color = TMCoral, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(TMLine)
                    .clickable(onClick = onEditSpendingBalance)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text("Edit spending balance →", color = TMInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp).padding(horizontal = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAddMoneyClick, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = TMCoral, contentColor = TMWhite), shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("＋  Add money", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Plan: ${salaryAllocation.spendingPercent}% spending · ${salaryAllocation.savingsPercent}% savings · ${salaryAllocation.emergencyPercent}% emergency",
                        modifier = Modifier.weight(1f),
                        fontSize = 12.sp,
                        color = TMMuted
                    )
                    TextButton(onClick = onEditSalaryAllocation) { Text("Adjust", color = TMCoral, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(TMWhite)
                .border(1.dp, TMLine, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
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
            ProfileMetric("SPENDING LEFT", TeenageMoneyRepository.displayMoney(spendingBalance, amountsMasked), Modifier.weight(1f))
            ProfileMetric("TOTAL SPENT", TeenageMoneyRepository.displayMoney(spent, amountsMasked), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMetric("EXPENSE ENTRIES", transactions.count { it.type == "expense" }.toString(), Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(TMWhite)
                .border(1.dp, TMLine, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            Column {
                Text("WHERE IT WENT", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp, color = TMLightMuted)
                Spacer(modifier = Modifier.height(12.dp))
                if (byCategory.isEmpty()) Text("Your category totals will appear after you log expenses.", fontSize = 13.sp, color = TMMuted)
                else byCategory.toList().sortedByDescending { it.second }.forEach { (category, amount) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(category, fontSize = 14.sp, color = TMInk, fontWeight = FontWeight.SemiBold)
                        Text(TeenageMoneyRepository.displayMoney(amount, amountsMasked), fontSize = 14.sp, color = TMInk, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(TMWhite)
                .border(1.dp, TMLine, RoundedCornerShape(22.dp))
                .pointerInput(Unit) {
                    var tapCount = 0
                    var lastTapAt = 0L
                    detectTapGestures {
                        val now = SystemClock.uptimeMillis()
                        tapCount = if (now - lastTapAt <= 600L) tapCount + 1 else 1
                        lastTapAt = now
                        if (tapCount == 3) {
                            tapCount = 0
                            onShowCreatorDetails()
                        }
                    }
                }
                .padding(20.dp)
        ) {
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

@Composable
private fun SnapshotRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = TMInk, fontWeight = FontWeight.Medium)
        Text(value, fontSize = 14.sp, color = TMInk, fontWeight = FontWeight.Bold)
    }
}
