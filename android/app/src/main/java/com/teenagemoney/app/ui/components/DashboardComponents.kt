package com.teenagemoney.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.selected as semanticsSelected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// TOP HEADER — Clean greeting with avatar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TopHeader(
    userName: String,
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TMDimens.Space20, vertical = TMDimens.Space14),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Hello, ${userName.ifBlank { "there" }}",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = TMInk,
                letterSpacing = (-0.2).sp
            )
        }
        Box(
            modifier = Modifier
                .size(TMDimens.AvatarMedium)
                .clip(CircleShape)
                .background(TMInk)
                .clickable(onClick = onProfileClick)
                .semantics {
                    contentDescription = "Open Profile"
                    role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "TM",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TMWhite
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// HERO BALANCE CARD — Prominent balance with hide/show toggle
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SpendingHeroCard(
    spendingBalance: Double,
    otherSpaceCount: Int,
    expenseCount: Int,
    amountsMasked: Boolean,
    onAddClick: () -> Unit,
    onPrivacyToggle: () -> Unit
) {
    val amountText = TeenageMoneyRepository.displayMoney(spendingBalance, amountsMasked)
    val privacyLabel = if (amountsMasked) "Show Balance" else "Hide Balance"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(TMDimens.CardElevation, RoundedCornerShape(TMDimens.Radius24))
            .clip(RoundedCornerShape(TMDimens.Radius24))
            .background(TMWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TMDimens.Space24)
        ) {
            // Eyebrow label
            Text(
                text = "Total Balance",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TMMuted,
                letterSpacing = 0.2.sp
            )

            Spacer(modifier = Modifier.height(TMDimens.Space4))

            // Hero amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = amountText,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = TMInk,
                    letterSpacing = (-1.5).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(TMDimens.Space12))

                // Hide/Show pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(TMDimens.Radius20))
                        .background(TMLine)
                        .clickable(onClick = onPrivacyToggle)
                        .semantics { contentDescription = privacyLabel }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (amountsMasked) "👁" else "👁‍🗨",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = privacyLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TMInk
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space20))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TMDimens.Space12)
            ) {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TMInk,
                        contentColor = TMWhite
                    ),
                    shape = RoundedCornerShape(TMDimens.Radius16),
                    modifier = Modifier
                        .weight(1f)
                        .height(TMDimens.ButtonHeight)
                ) {
                    Text("Add money", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// LOG EXPENSE BUTTON — Primary action
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun LogExpenseButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = TMCoral,
            contentColor = TMWhite,
            disabledContainerColor = TMCoral.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(TMDimens.Radius16),
        modifier = Modifier
            .fillMaxWidth()
            .height(TMDimens.ButtonHeight)
            .semantics { contentDescription = "Log expense" }
    ) {
        Text(
            text = "Log expense",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// WEEK RHYTHM CARD — Weekly spending visualization
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeekRhythmCard(
    spentThisWeek: Double,
    dailyTotals: List<Double>,
    selectedDayIndex: Int,
    selectedDayTransactions: List<TransactionItem>,
    amountsMasked: Boolean,
    onViewAllClick: () -> Unit,
    onDayClick: (Int) -> Unit = {}
) {
    val today = LocalDate.now(ZoneId.systemDefault())
    val weekDays = remember(today) {
        val monday = today.with(DayOfWeek.MONDAY)
        (0..6).map { offset ->
            val date = monday.plusDays(offset.toLong())
            Triple(
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                date.dayOfMonth.toString(),
                date
            )
        }
    }
    val maxDaily = dailyTotals.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
    val selectedTotal = selectedDayTransactions.sumOf { it.amount }
    val selectedMeta = weekDays.getOrNull(selectedDayIndex)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(TMDimens.CardElevation, RoundedCornerShape(TMDimens.Radius24))
            .clip(RoundedCornerShape(TMDimens.Radius24))
            .background(TMWhite)
            .padding(TMDimens.Space20)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "This Week",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TMMuted
                    )
                    Spacer(modifier = Modifier.height(TMDimens.Space2))
                    Text(
                        text = TeenageMoneyRepository.displayMoney(spentThisWeek, amountsMasked) + " spent",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMInk,
                        letterSpacing = (-0.3).sp
                    )
                }
                TextButton(
                    onClick = onViewAllClick,
                    modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                ) {
                    Text(
                        text = "View all",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TMMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space20))

            // Day circles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TMDimens.Space4)
            ) {
                weekDays.forEachIndexed { i, (label, num, date) ->
                    val isSelected = i == selectedDayIndex
                    val hasSpending = dailyTotals.getOrElse(i) { 0.0 } > 0.0
                    val dayAmount = dailyTotals.getOrElse(i) { 0.0 }
                    val weekdayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    val spentAnnounce = if (hasSpending) {
                        TeenageMoneyRepository.displayMoney(dayAmount, amountsMasked) + " spent"
                    } else {
                        "no spending"
                    }
                    val selectedAnnounce = if (isSelected) "selected" else "not selected"

                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) TMInk else TMLine,
                        animationSpec = tween(200),
                        label = "dayBg"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) TMWhite else TMInk,
                        animationSpec = tween(200),
                        label = "dayText"
                    )
                    val labelColor by animateColorAsState(
                        targetValue = if (isSelected) TMWhite.copy(alpha = 0.7f) else TMMuted,
                        animationSpec = tween(200),
                        label = "dayLabel"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(TMDimens.Radius16))
                            .clickable { onDayClick(i) }
                            .semantics {
                                contentDescription = "$weekdayName $num, $selectedAnnounce, $spentAnnounce"
                                selected = isSelected
                                role = Role.Button
                            }
                            .padding(vertical = TMDimens.Space4)
                    ) {
                        Text(
                            text = label.take(2),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = labelColor
                        )
                        Spacer(modifier = Modifier.height(TMDimens.Space4))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = num,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                        // Spending indicator dot
                        Spacer(modifier = Modifier.height(TMDimens.Space4))
                        if (hasSpending) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) TMCoral else TMMuted.copy(alpha = 0.3f))
                            )
                        } else {
                            Spacer(modifier = Modifier.size(5.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space20))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TMDimens.Divider)
                    .background(TMLine)
            )

            Spacer(modifier = Modifier.height(TMDimens.Space16))

            // Selected day detail
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedMeta == null) "Selected day"
                        else "${selectedMeta.third.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${selectedMeta.second}",
                        fontSize = 13.sp,
                        color = TMMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(TMDimens.Space4))
                    Text(
                        text = TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMInk,
                        letterSpacing = (-0.8).sp
                    )
                }

                // Mini bar chart
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(36.dp)
                ) {
                    dailyTotals.forEachIndexed { i, total ->
                        val frac = (total / maxDaily).toFloat().coerceIn(0.06f, 1.0f)
                        val barH = (frac * 36).dp
                        val isBarSelected = i == selectedDayIndex
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(barH)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isBarSelected -> TMInk
                                        total > 0.0 -> TMLine
                                        else -> TMLine.copy(alpha = 0.4f)
                                    }
                                )
                        )
                    }
                }
            }

            // Transaction list for selected day
            Spacer(modifier = Modifier.height(TMDimens.Space12))
            if (selectedDayTransactions.isEmpty()) {
                Text(
                    text = "No spending on this day.",
                    fontSize = 13.sp,
                    color = TMMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space4)) {
                    selectedDayTransactions.take(4).forEach { tx ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = tx.title,
                                fontSize = 13.sp,
                                color = TMInk,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(TMDimens.Space8))
                            Text(
                                text = TeenageMoneyRepository.displayMoney(tx.amount, amountsMasked),
                                fontSize = 13.sp,
                                color = TMInk,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// PERIOD SWITCHER — Week / Month toggle
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun PeriodSwitcher(
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(TMDimens.Radius12))
            .background(TMLine)
            .padding(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Week", "Month").forEach { period ->
            val active = period == selected
            Text(
                text = period,
                modifier = Modifier
                    .clip(RoundedCornerShape(TMDimens.Radius8))
                    .background(if (active) TMWhite else Color.Transparent)
                    .clickable { onSelected(period) }
                    .semantics {
                        contentDescription = "$period view"
                        semanticsSelected = active
                        role = Role.Tab
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                fontSize = 13.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                color = if (active) TMInk else TMMuted
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MONTH CALENDAR CARD — Monthly spending overview
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun MonthCalendarCard(
    monthTotal: Double,
    dailyTotals: List<Double>,
    selectedDay: LocalDate,
    selectedDayTransactions: List<TransactionItem>,
    amountsMasked: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now(ZoneId.systemDefault())
    val firstDay = today.withDayOfMonth(1)
    val calendarCells = buildList<LocalDate?> {
        repeat(firstDay.dayOfWeek.value - 1) { add(null) }
        (1..today.lengthOfMonth()).forEach { add(firstDay.withDayOfMonth(it)) }
    }
    val maxDaily = dailyTotals.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
    val selectedTotal = selectedDayTransactions.sumOf { it.amount }
    val monthLabel = today.format(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH))
    val selectedLabel = selectedDay.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH))

    Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space12)) {
        // Month header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = monthLabel,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                color = TMInk
            )
            Text(
                text = today.format(DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH)),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TMMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(TMDimens.Radius20))
                    .background(TMLine)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // Selected day summary pill
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(TMDimens.CardElevation, RoundedCornerShape(TMDimens.Radius16))
                .clip(RoundedCornerShape(TMDimens.Radius16))
                .background(TMWhite)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TMInk)
            Text(
                text = if (selectedDay.isAfter(today)) "Upcoming" else "${TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked)} spent",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selectedDay.isAfter(today)) TMMuted else TMCoral
            )
        }

        // Calendar grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(TMDimens.Radius16))
                .background(Color(0xFFFCFCFC))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { index, label ->
                        Text(
                            text = label,
                            modifier = Modifier.weight(1f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 11.sp,
                            fontWeight = if (index == today.dayOfWeek.value - 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (index == today.dayOfWeek.value - 1) TMCoral else TMMuted
                        )
                    }
                }
                calendarCells.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { date ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                if (date == null) {
                                    Spacer(modifier = Modifier.size(38.dp))
                                } else {
                                    val amount = dailyTotals.getOrElse(date.dayOfMonth - 1) { 0.0 }
                                    val isFuture = date.isAfter(today)
                                    val isSelected = date == selectedDay

                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> TMInk
                                                    !isFuture && amount > 0.0 -> TMCoralLight
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .clickable { onDayClick(date) }
                                            .semantics {
                                                contentDescription = if (isFuture) {
                                                    "$date, upcoming"
                                                } else {
                                                    "$date, ${TeenageMoneyRepository.displayMoney(amount, amountsMasked)} spent"
                                                }
                                                selected = isSelected
                                                role = Role.Button
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = when {
                                                isSelected -> TMWhite
                                                isFuture -> TMMuted.copy(alpha = 0.5f)
                                                else -> TMInk
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Month total summary
        Column {
            Text(
                text = TeenageMoneyRepository.displayMoney(monthTotal, amountsMasked) + " this month",
                fontSize = 13.sp,
                color = TMMuted,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked),
                fontSize = 28.sp,
                color = TMInk,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.8).sp
            )
            Text(
                text = "${selectedDayTransactions.size} transaction${if (selectedDayTransactions.size == 1) "" else "s"} on $selectedLabel",
                fontSize = 13.sp,
                color = TMMuted
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MONEY SPACES — Stacked cards for Spending, Savings, Emergency
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SpacesSection(
    spaces: List<MoneySpace>,
    amountsMasked: Boolean,
    onSpaceClick: (MoneySpace) -> Unit,
    onAddSpaceClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space12)) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your Spaces",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = "Where your money lives",
                    fontSize = 13.sp,
                    color = TMMuted
                )
            }
            TextButton(
                onClick = onAddSpaceClick,
                modifier = Modifier.heightIn(min = TMDimens.MinTouch)
            ) {
                Text(
                    text = "+ New",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TMMuted
                )
            }
        }

        // Space cards
        spaces.forEach { space ->
            val (bg, border, accent) = when (space.name) {
                "Spending" -> Triple(TMPeachBg, TMPeachBorder, TMPeachText)
                "Savings" -> Triple(TMMintBg, TMMintBorder, TMMintText)
                else -> Triple(TMLavenderBg, TMLavenderBorder, TMLavenderText)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(TMDimens.CardElevation, RoundedCornerShape(TMDimens.Radius20))
                    .clip(RoundedCornerShape(TMDimens.Radius20))
                    .background(bg)
                    .clickable { onSpaceClick(space) }
                    .semantics { contentDescription = "Adjust ${space.name} balance" }
                    .padding(TMDimens.Space20)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = space.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TMInk
                        )
                        Text(
                            text = space.note,
                            fontSize = 12.sp,
                            color = TMMuted
                        )
                    }
                    Text(
                        text = TeenageMoneyRepository.displayMoney(space.balance, amountsMasked),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        letterSpacing = (-0.3).sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// RECENT ACTIVITY — Grouped transactions
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun RecentActivitySection(
    transactions: List<TransactionItem>,
    amountsMasked: Boolean,
    onSeeAllClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space12)) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Recent Activity",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk,
                    letterSpacing = (-0.3).sp
                )
            }
            if (transactions.isNotEmpty()) {
                TextButton(
                    onClick = onSeeAllClick,
                    modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                ) {
                    Text(
                        text = "See all",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TMMuted
                    )
                }
            }
        }

        // Transaction list card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(TMDimens.CardElevation, RoundedCornerShape(TMDimens.Radius20))
                .clip(RoundedCornerShape(TMDimens.Radius20))
                .background(TMWhite)
                .padding(TMDimens.Space16)
        ) {
            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(TMDimens.Space16),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No transactions yet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TMInk
                    )
                    Spacer(modifier = Modifier.height(TMDimens.Space4))
                    Text(
                        text = "Log an expense or add money to get started.",
                        fontSize = 13.sp,
                        color = TMMuted
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space2)) {
                    transactions.take(4).forEachIndexed { index, t ->
                        TransactionRow(
                            transaction = t,
                            amountsMasked = amountsMasked,
                            onClick = { onTransactionClick(t) }
                        )
                        if (index < transactions.take(4).size - 1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 54.dp)
                                    .height(TMDimens.Divider)
                                    .background(TMLine)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TRANSACTION ROW — Individual transaction item
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TransactionRow(
    transaction: TransactionItem,
    amountsMasked: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val (bg, fg) = when (transaction.color) {
        "orange" -> Pair(TMIconsFoodBg, TMIconsFoodFg)
        "blue" -> Pair(TMIconsTransportBg, TMIconsTransportFg)
        "mint" -> Pair(TMIconsEducationBg, TMIconsEducationFg)
        "violet" -> Pair(TMIconsShoppingBg, TMIconsShoppingFg)
        else -> Pair(TMCoralLight, TMCoral)
    }
    val isIncome = transaction.type == "income"
    val amountText = (if (isIncome) "+" else "−") +
        TeenageMoneyRepository.displayMoney(transaction.amount, amountsMasked)
    val rowDescription = "${transaction.title}, ${transaction.category}, ${transaction.day}, $amountText"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = TMDimens.MinTouch)
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(TMDimens.Radius12))
                        .clickable(onClick = onClick)
                } else Modifier
            )
            .semantics { contentDescription = rowDescription }
            .padding(vertical = TMDimens.Space8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon
        Box(
            modifier = Modifier
                .size(TMDimens.IconCircle)
                .clip(RoundedCornerShape(TMDimens.Radius12))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transaction.icon, fontSize = 18.sp, color = fg)
        }
        Spacer(modifier = Modifier.width(TMDimens.Space12))

        // Title & category
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TMInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.category,
                fontSize = 12.sp,
                color = TMMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(TMDimens.Space8))

        // Amount & date
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amountText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isIncome) TMSuccess else TMInk
            )
            Text(
                text = transaction.day,
                fontSize = 11.sp,
                color = TMMuted
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BOTTOM NAV BAR — Minimal pill navigation
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BottomNavBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf("Home", "Activity")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = TMDimens.Space24, vertical = TMDimens.Space12)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(TMDimens.CardElevationHigh, RoundedCornerShape(TMDimens.Radius28))
                .clip(RoundedCornerShape(TMDimens.Radius28))
                .background(TMWhite)
                .padding(horizontal = TMDimens.Space8, vertical = TMDimens.Space6),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { title ->
                val isSelected = activeTab == title
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) TMInk else Color.Transparent,
                    animationSpec = tween(250),
                    label = "navBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) TMWhite else TMMuted,
                    animationSpec = tween(250),
                    label = "navText"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(TMDimens.Radius20))
                        .background(bgColor)
                        .clickable { onTabSelected(title) }
                        .sizeIn(minWidth = 80.dp, minHeight = TMDimens.MinTouch)
                        .semantics {
                            contentDescription = "Open $title"
                            selected = isSelected
                            stateDescription = if (isSelected) "selected" else "not selected"
                            role = Role.Tab
                        }
                        .padding(horizontal = TMDimens.Space16, vertical = TMDimens.Space8),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
