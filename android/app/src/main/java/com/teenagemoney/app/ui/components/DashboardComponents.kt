package com.teenagemoney.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.R
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.SavingsGoal
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.ui.theme.TMCardDark
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
import com.teenagemoney.app.ui.theme.TMOnDarkMuted
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
    userName: String
) {
    val today = LocalDate.now()
    val dateLabel = remember(today) {
        val dayName = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        val month = today.format(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH))
        "$dayName, ${today.dayOfMonth} $month"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = TMDimens.Space16, vertical = TMDimens.Space12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_teenage_money),
            contentDescription = "Teenage Money",
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
        )
        Spacer(modifier = Modifier.width(TMDimens.Space12))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TMMuted
            )
            Text(
                text = "Hey, $userName",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMInk,
                letterSpacing = (-0.5).sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
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
            .heightIn(min = 168.dp)
            .shadow(TMDimens.Elevation, RoundedCornerShape(TMDimens.Radius24))
            .clip(RoundedCornerShape(TMDimens.Radius24))
            .background(TMCardDark)
    ) {
        OrbitVisual(Modifier.align(Alignment.CenterEnd).fillMaxHeight().fillMaxWidth(0.55f))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0.0f to TMCardDark,
                        0.58f to TMCardDark.copy(alpha = 0.88f),
                        1.0f to Color.Transparent
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(TMDimens.Space16),
            verticalArrangement = Arrangement.spacedBy(TMDimens.Space12)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(TMDimens.Space8)) {
                Text(
                    text = "Available to spend",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TMWhite
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(TMDimens.Space8)
                ) {
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = TMDimens.MinTouch, minHeight = TMDimens.MinTouch)
                            .clip(RoundedCornerShape(TMDimens.Radius16))
                            .clickable { onPrivacyToggle() }
                            .semantics {
                                contentDescription = privacyLabel
                                role = Role.Button
                            }
                            .padding(horizontal = TMDimens.Space8),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (amountsMasked) "Show" else "Hide",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TMOnDarkMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .sizeIn(minWidth = TMDimens.MinTouch, minHeight = TMDimens.MinTouch)
                            .clip(RoundedCornerShape(TMDimens.Radius16))
                            .background(Color.White.copy(alpha = 0.14f))
                            .clickable { onAddClick() }
                            .semantics {
                                contentDescription = "Add money to Spending"
                                role = Role.Button
                            }
                            .padding(horizontal = TMDimens.Space12),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add money",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TMWhite
                        )
                    }
                }
            }

            Text(
                text = amountText,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMWhite,
                letterSpacing = (-1).sp,
                maxLines = 2,
                overflow = TextOverflow.Clip
            )
            Text(
                text = scopeLine,
                fontSize = 13.sp,
                color = TMOnDarkMuted,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = statusLine,
                fontSize = 13.sp,
                color = TMOnDarkMuted,
                fontWeight = FontWeight.Medium
            )
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
            .fillMaxWidth()
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
                        text = "Your rhythm",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TMMuted
                    )
                    Text(
                        text = "This week",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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
                            .clip(RoundedCornerShape(TMDimens.Radius12))
                            .background(if (isSelected) Color(0xFFEBE8F9) else Color.Transparent)
                            .border(
                                width = if (isSelected) 1.5.dp else 0.dp,
                                color = if (isSelected) TMInk else Color.Transparent,
                                shape = RoundedCornerShape(TMDimens.Radius12)
                            )
                            .clickable { onDayClick(i) }
                            .semantics {
                                contentDescription = "$weekdayName $num, $selectedAnnounce, $spentAnnounce"
                                selected = isSelected
                                role = Role.Button
                            }
                            .padding(vertical = TMDimens.Space8)
                    ) {
                        Text(
                            text = label.take(2),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = TMMuted
                        )
                        Spacer(modifier = Modifier.height(TMDimens.Space4))
                        Text(
                            text = num,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (isSelected) TMPurple else TMInk
                        )
                        Spacer(modifier = Modifier.height(TMDimens.Space4))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (hasSpending) TMCoral else Color.Transparent)
                        )
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
                        text = "Spent this week",
                        fontSize = 13.sp,
                        color = TMMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(TMDimens.Space4))
                    Text(
                        text = TeenageMoneyRepository.displayMoney(spentThisWeek, amountsMasked),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TMInk
                    )
                    if (selectedMeta != null) {
                        Spacer(modifier = Modifier.height(TMDimens.Space4))
                        Text(
                            text = "Selected: ${selectedMeta.third.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${selectedMeta.second} · ${TeenageMoneyRepository.displayMoney(selectedTotal, amountsMasked)} spent",
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
fun OneThingGoalCard(
    goal: SavingsGoal?,
    onCardClick: () -> Unit,
    onAddGoalClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TMDimens.Radius16))
            .background(TMWhite)
            .border(1.dp, TMLine, RoundedCornerShape(TMDimens.Radius16))
            .clickable { if (goal != null) onCardClick() else onAddGoalClick() }
            .padding(TMDimens.Space16)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "One thing at a time",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TMMuted
                )
                if (goal != null) {
                    TextButton(
                        onClick = onCardClick,
                        modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                    ) {
                        Text(
                            text = "See all",
                            fontSize = 13.sp,
                            color = TMInk
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(TMDimens.Space8))

            if (goal == null) {
                Text(
                    text = "No goal yet",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk
                )
                Spacer(modifier = Modifier.height(TMDimens.Space4))
                Text(
                    text = "Set something worth saving for.",
                    fontSize = 13.sp,
                    color = TMMuted
                )
                Spacer(modifier = Modifier.height(TMDimens.Space12))
                Button(
                    onClick = onAddGoalClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TMCoral, contentColor = TMWhite),
                    shape = RoundedCornerShape(TMDimens.Radius12),
                    modifier = Modifier.heightIn(min = TMDimens.MinTouch)
                ) {
                    Text(
                        text = "Create a goal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                val pct = minOf(100, ((goal.saved / maxOf(1.0, goal.target)) * 100).toInt())

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TMInk
                        )
                        Text(
                            text = if (pct >= 100) "Goal reached" else "${100 - pct}% remaining",
                            fontSize = 13.sp,
                            color = TMMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(TMPeachBg)
                            .border(1.5.dp, TMPeachBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$pct%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TMCoral
                            )
                            Text(text = "saved", fontSize = 11.sp, color = TMMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(TMDimens.Space12))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(TMLine)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(pct / 100f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(TMCoral)
                    )
                }

                Spacer(modifier = Modifier.height(TMDimens.Space8))

                Text(
                    text = "${TeenageMoneyRepository.formatMoney(goal.saved)} of ${TeenageMoneyRepository.formatMoney(goal.target)}",
                    fontSize = 13.sp,
                    color = TMMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val items = listOf(
        Pair("Home", "⌂"),
        Pair("Activity", "◷"),
        Pair("Goals", "◇"),
        Pair("Profile", "◎")
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TMWhite)
            .border(0.5.dp, TMLine)
            .navigationBarsPadding()
            .padding(vertical = TMDimens.Space8)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (title, icon) ->
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
                        text = icon,
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TMCoral else TMMuted
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) TMInk else TMMuted
                    )
                    Spacer(modifier = Modifier.height(TMDimens.Space4))
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isSelected) TMInk else Color.Transparent)
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
