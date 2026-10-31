package com.teenagemoney.app.ui.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.ui.theme.TMCoral
import com.teenagemoney.app.ui.theme.TMDimens
import com.teenagemoney.app.ui.theme.TMIconsEducationBg
import com.teenagemoney.app.ui.theme.TMIconsEducationFg
import com.teenagemoney.app.ui.theme.TMIconsFoodBg
import com.teenagemoney.app.ui.theme.TMIconsFoodFg
import com.teenagemoney.app.ui.theme.TMIconsShoppingBg
import com.teenagemoney.app.ui.theme.TMIconsShoppingFg
import com.teenagemoney.app.ui.theme.TMIconsTransportBg
import com.teenagemoney.app.ui.theme.TMIconsTransportFg
import com.teenagemoney.app.ui.theme.TMInk
import com.teenagemoney.app.ui.theme.TMLavenderBg
import com.teenagemoney.app.ui.theme.TMLavenderBorder
import com.teenagemoney.app.ui.theme.TMLine
import com.teenagemoney.app.ui.theme.TMMintBg
import com.teenagemoney.app.ui.theme.TMMintBorder
import com.teenagemoney.app.ui.theme.TMMuted
import com.teenagemoney.app.ui.theme.TMPeachBg
import com.teenagemoney.app.ui.theme.TMPeachBorder
import com.teenagemoney.app.ui.theme.TMPeachText
import com.teenagemoney.app.ui.theme.TMPurple
import com.teenagemoney.app.ui.theme.TMWhite
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TopHeader(
    userName: String,
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = TMDimens.Space12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "teenage",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMInk,
                letterSpacing = (-0.6).sp
            )
            Text(
                text = "money",
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMInk,
                letterSpacing = (-0.6).sp
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(TMCoral)
                .clickable(onClick = onProfileClick)
                .semantics {
                    contentDescription = "Open Profile"
                    role = Role.Button
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "TM",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMWhite
            )
        }
    }
}

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
    val scopeLine = if (otherSpaceCount <= 0) {
        "Available in your Spending space"
    } else {
        val other = if (otherSpaceCount == 1) "1 other space" else "$otherSpaceCount other spaces"
        "Available in Spending · $other not included"
    }
    val statusLine = when {
        spendingBalance <= 0.0 && expenseCount == 0 -> "Add money to get started"
        expenseCount == 0 -> "No expenses logged yet"
        expenseCount == 1 -> "1 expense logged"
        else -> "$expenseCount expenses logged"
    }
    val privacyLabel = if (amountsMasked) "Show amounts" else "Hide amounts"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 190.dp)
            .shadow(3.dp, RoundedCornerShape(TMDimens.Radius24))
            .clip(RoundedCornerShape(TMDimens.Radius24))
            .background(TMWhite)
            .border(1.dp, TMLine, RoundedCornerShape(TMDimens.Radius24))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TMDimens.Space16),
            verticalArrangement = Arrangement.spacedBy(TMDimens.Space8)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "AVAILABLE TO SPEND",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.1.sp,
                    color = TMMuted
                )
                Spacer(modifier = Modifier.width(TMDimens.Space8))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(TMCoral)
                ) {
                }
            }

            Text(
                text = amountText,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMInk,
                letterSpacing = (-1.5).sp,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
            Text(
                text = "$scopeLine · $statusLine",
                fontSize = 13.sp,
                color = TMMuted,
                fontWeight = FontWeight.Medium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TMDimens.Space8)
            ) {
                TextButton(
                    onClick = onPrivacyToggle,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = TMDimens.MinTouch)
                        .semantics { contentDescription = privacyLabel }
                ) {
                    Text(
                        text = if (amountsMasked) "Show amount" else "Hide amount",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TMInk
                    )
                }
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TMInk, contentColor = TMWhite),
                    shape = RoundedCornerShape(TMDimens.Radius16),
                    modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                ) {
                    Text("Add money", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

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
            .width(178.dp)
            .heightIn(min = TMDimens.MinTouch)
            .semantics { contentDescription = "Log expense" }
    ) {
        Text(
            text = "Log expense",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

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
            .clip(RoundedCornerShape(TMDimens.Radius24))
            .background(TMWhite)
            .border(1.dp, TMLine, RoundedCornerShape(TMDimens.Radius24))
            .padding(TMDimens.Space16)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "YOUR WEEK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.1.sp,
                        color = TMMuted
                    )
                    Text(
                        text = TeenageMoneyRepository.displayMoney(spentThisWeek, amountsMasked) + " spent",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TMInk
                    )
                }
                TextButton(
                    onClick = onViewAllClick,
                    modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                ) {
                    Text(
                        text = "View all",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TMInk
                    )
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space16))

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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = TMDimens.MinTouch)
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) TMInk else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onDayClick(i) }
                            .semantics {
                                contentDescription = "$weekdayName $num, $selectedAnnounce, $spentAnnounce"
                                selected = isSelected
                                role = Role.Button
                            }
                            .padding(vertical = TMDimens.Space4)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1EFEB))
                        ) {
                            if (hasSpending) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight((dayAmount / maxDaily).toFloat().coerceIn(0.08f, 1f))
                                        .align(Alignment.BottomCenter)
                                        .background(if (isSelected) TMCoral else Color(0xFFF0B0A0))
                                )
                            }
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label.take(2),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) TMInk else TMMuted
                                )
                                Text(
                                    text = num,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TMInk
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space16))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TMLine)
            )
            Spacer(modifier = Modifier.height(TMDimens.Space16))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (selectedMeta == null) "Selected day" else "${selectedMeta.third.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${selectedMeta.second}",
                        fontSize = 13.sp,
                        color = TMMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(TMDimens.Space4))
                    Text(
                        text = TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TMInk
                    )
                    if (selectedMeta != null) {
                        Spacer(modifier = Modifier.height(TMDimens.Space4))
                        Text(
                            text = "${selectedDayTransactions.size} transaction${if (selectedDayTransactions.size == 1) "" else "s"}",
                            fontSize = 13.sp,
                            color = TMMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(TMDimens.Space4),
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.height(34.dp)
                ) {
                    dailyTotals.forEachIndexed { i, total ->
                        val frac = (total / maxDaily).toFloat().coerceIn(0.03f, 1.0f)
                        val barH = (frac * 34).dp
                        val isSelected = i == selectedDayIndex
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(barH)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isSelected -> TMPurple
                                        total > 0.0 -> Color(0xFFD4CCF2)
                                        else -> Color(0xFFEEECF5)
                                    }
                                )
                                .then(
                                    if (isSelected) Modifier.border(1.dp, TMInk, RoundedCornerShape(3.dp))
                                    else Modifier
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space12))
            if (selectedDayTransactions.isEmpty()) {
                Text(
                    text = "No spending recorded on this day.",
                    fontSize = 13.sp,
                    color = TMMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space8)) {
                    selectedDayTransactions.take(4).forEach { tx ->
                        Text(
                            text = "${tx.title}  ·  ${TeenageMoneyRepository.displayMoney(tx.amount, amountsMasked)}",
                            fontSize = 13.sp,
                            color = TMInk,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodSwitcher(
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFFEFEDEB))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Week", "Month").forEach { period ->
            val active = period == selected
            Text(
                text = period,
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (active) TMWhite else Color.Transparent)
                    .clickable { onSelected(period) }
                    .semantics {
                        contentDescription = "$period view"
                        semanticsSelected = active
                        role = Role.Tab
                    }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                fontSize = 12.sp,
                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                color = if (active) TMInk else TMMuted
            )
        }
    }
}

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
    val monthLabel = today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)).uppercase(Locale.ENGLISH)
    val selectedLabel = selectedDay.format(DateTimeFormatter.ofPattern("EEE d MMM", Locale.ENGLISH))

    Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space12)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = monthLabel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp,
                    color = TMInk
                )
                Spacer(modifier = Modifier.width(TMDimens.Space8))
                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(TMCoral))
            }
            Text(
                text = today.format(DateTimeFormatter.ofPattern("EEE · d", Locale.ENGLISH)).uppercase(Locale.ENGLISH),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = TMMuted,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1EFED))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(TMWhite)
                .shadow(2.dp, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TMCoral))
                Spacer(modifier = Modifier.width(TMDimens.Space8))
                Text(selectedLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TMInk)
            }
            Text(
                text = if (selectedDay.isAfter(today)) "Upcoming day" else "${TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked)} spent",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedDay.isAfter(today)) TMMuted else TMCoral
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(TMDimens.Radius16))
                .background(Color(0xFFFDFCFB))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                    Spacer(modifier = Modifier.size(40.dp))
                                } else {
                                    val amount = dailyTotals.getOrElse(date.dayOfMonth - 1) { 0.0 }
                                    val isFuture = date.isAfter(today)
                                    val isSelected = date == selectedDay
                                    val fill = (amount / maxDaily).toFloat().coerceIn(0.05f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isFuture -> Color(0xFFF3F1EF)
                                                    amount > 0.0 -> Color(0xFFF2EFEC)
                                                    else -> Color(0xFFEDEBE8)
                                                }
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) TMInk else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable { onDayClick(date) }
                                            .semantics {
                                                contentDescription = if (isFuture) {
                                                    "$date, upcoming day"
                                                } else {
                                                    "$date, ${TeenageMoneyRepository.displayMoney(amount, amountsMasked)} spent"
                                                }
                                                selected = isSelected
                                                role = Role.Button
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!isFuture && amount > 0.0) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .fillMaxHeight(fill)
                                                    .align(Alignment.BottomCenter)
                                                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                                                    .background(TMCoral)
                                            )
                                        }
                                        Text(
                                            text = date.dayOfMonth.toString(),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                            color = if (!isFuture && amount > maxDaily * 0.55) TMWhite else if (isFuture) TMMuted else TMInk
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${TeenageMoneyRepository.displayMoney(monthTotal, amountsMasked)} spent this month",
                    fontSize = 13.sp,
                    color = TMMuted,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked),
                    fontSize = 28.sp,
                    color = TMInk,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "${selectedDayTransactions.size} transaction${if (selectedDayTransactions.size == 1) "" else "s"} on $selectedLabel",
                    fontSize = 13.sp,
                    color = TMMuted
                )
            }
        }
    }
}

@Composable
fun SpacesSection(
    spaces: List<MoneySpace>,
    amountsMasked: Boolean,
    onSpaceClick: (MoneySpace) -> Unit,
    onAddSpaceClick: () -> Unit
) {
    val spendingSpace = spaces.find { it.name == "Spending" }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Your spaces",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TMMuted
                )
                Text(
                    text = "Where your money lives",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
            }

            TextButton(
                onClick = onAddSpaceClick,
                modifier = Modifier
                    .heightIn(min = TMDimens.MinTouch)
                    .semantics { contentDescription = "New space" }
            ) {
                Text(
                    text = "New space",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TMInk
                )
            }
        }

        Spacer(modifier = Modifier.height(TMDimens.Space12))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(TMDimens.Radius16))
                .background(TMPeachBg)
                .border(1.dp, TMPeachBorder, RoundedCornerShape(TMDimens.Radius16))
                .clickable(enabled = spendingSpace != null) {
                    spendingSpace?.let(onSpaceClick)
                }
                .semantics { contentDescription = "Adjust Spending balance" }
                .padding(TMDimens.Space16)
        ) {
            Column {
                Text(
                    text = "Spending",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
                Text(
                    text = spendingSpace?.note ?: "Everyday money",
                    fontSize = 13.sp,
                    color = TMMuted
                )
                Spacer(modifier = Modifier.height(TMDimens.Space8))
                Text(
                    text = TeenageMoneyRepository.displayMoney(spendingSpace?.balance ?: 0.0, amountsMasked),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TMPeachText
                )
            }
        }

        Spacer(modifier = Modifier.height(TMDimens.Space12))

        val otherSpaces = spaces.filter { it.name != "Spending" }
        otherSpaces.chunked(2).forEach { rowSpaces ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TMDimens.Space12)
            ) {
                rowSpaces.forEach { space ->
                    val isMint = space.name == "Savings"
                    val bg = if (isMint) TMMintBg else TMLavenderBg
                    val border = if (isMint) TMMintBorder else TMLavenderBorder
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(TMDimens.Radius16))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(TMDimens.Radius16))
                            .clickable { onSpaceClick(space) }
                            .semantics { contentDescription = "Adjust ${space.name} balance" }
                            .padding(TMDimens.Space16)
                    ) {
                        Column {
                            Text(text = space.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(TMDimens.Space8))
                            Text(
                                text = space.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TMInk,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = space.note,
                                fontSize = 12.sp,
                                color = TMMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(TMDimens.Space8))
                            Text(
                                text = TeenageMoneyRepository.displayMoney(space.balance, amountsMasked),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TMInk
                            )
                        }
                    }
                }
                if (rowSpaces.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(TMDimens.Space12))
        }
    }
}

@Composable
fun RecentActivitySection(
    transactions: List<TransactionItem>,
    amountsMasked: Boolean,
    onSeeAllClick: () -> Unit,
    onTransactionClick: (TransactionItem) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recent activity",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TMMuted
                )
                Text(
                    text = "Your money trail",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
            }
            if (transactions.isNotEmpty()) {
                TextButton(
                    onClick = onSeeAllClick,
                    modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                ) {
                    Text(
                        text = "See all",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TMInk
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(TMDimens.Space12))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(TMDimens.Radius16))
                .background(TMWhite)
                .border(1.dp, TMLine, RoundedCornerShape(TMDimens.Radius16))
                .padding(TMDimens.Space16)
        ) {
            if (transactions.isEmpty()) {
                Text(
                    text = "No transactions yet.\nTap Log expense or Add money to get started.",
                    fontSize = 13.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(TMDimens.Space12)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space8)) {
                    transactions.take(3).forEach { t ->
                        TransactionRow(
                            transaction = t,
                            amountsMasked = amountsMasked,
                            onClick = { onTransactionClick(t) }
                        )
                    }
                }
            }
        }
    }
}

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
        else -> Pair(TMPeachBg, TMCoral)
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
            .padding(vertical = TMDimens.Space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transaction.icon, fontSize = 18.sp, color = fg)
        }
        Spacer(modifier = Modifier.width(TMDimens.Space12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TMInk,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = transaction.category,
                fontSize = 13.sp,
                color = TMMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(TMDimens.Space8))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = amountText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (isIncome) Color(0xFF2E9A66) else TMInk
            )
            Text(
                text = transaction.day,
                fontSize = 12.sp,
                color = TMMuted
            )
        }
    }
}

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
            .padding(horizontal = TMDimens.Space16, vertical = TMDimens.Space8)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(TMWhite)
                .border(1.dp, TMLine, RoundedCornerShape(32.dp))
                .padding(horizontal = TMDimens.Space8, vertical = TMDimens.Space4),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { title ->
                val isSelected = activeTab == title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onTabSelected(title) }
                        .sizeIn(minWidth = 64.dp, minHeight = TMDimens.MinTouch)
                        .semantics {
                            contentDescription = "Open $title"
                            selected = isSelected
                            stateDescription = if (isSelected) "selected" else "not selected"
                            role = Role.Tab
                        }
                        .padding(horizontal = TMDimens.Space8, vertical = TMDimens.Space4)
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) TMCoral else TMMuted
                    )
                }
            }
        }
    }
}

@Preview(name = "Hero 360", widthDp = 360, showBackground = true, backgroundColor = 0xFFFBFAF8)
@Composable
private fun SpendingHeroCardPreview() {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SpendingHeroCard(
            spendingBalance = 1234567.89,
            otherSpaceCount = 2,
            expenseCount = 4,
            amountsMasked = false,
            onAddClick = {},
            onPrivacyToggle = {}
        )
        LogExpenseButton(onClick = {})
    }
}

@Preview(name = "Hero fontScale 2", widthDp = 360, fontScale = 2.0f, showBackground = true)
@Composable
private fun SpendingHeroCardLargeFontPreview() {
    SpendingHeroCard(
        spendingBalance = 125000.0,
        otherSpaceCount = 2,
        expenseCount = 1,
        amountsMasked = false,
        onAddClick = {},
        onPrivacyToggle = {}
    )
}
