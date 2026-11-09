package com.teenagemoney.app.ui

import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.ui.components.BottomSheetWrapper
import com.teenagemoney.app.ui.components.SheetInputField
import com.teenagemoney.app.ui.theme.TMCardDark
import com.teenagemoney.app.ui.theme.TMCoral
import com.teenagemoney.app.ui.theme.TMCoralLight
import com.teenagemoney.app.ui.theme.TMInk
import com.teenagemoney.app.ui.theme.TMLine
import com.teenagemoney.app.ui.theme.TMMintBg
import com.teenagemoney.app.ui.theme.TMMintBorder
import com.teenagemoney.app.ui.theme.TMMintText
import com.teenagemoney.app.ui.theme.TMMuted
import com.teenagemoney.app.ui.theme.TMPeachBg
import com.teenagemoney.app.ui.theme.TMPeachBorder
import com.teenagemoney.app.ui.theme.TMPeachText
import com.teenagemoney.app.ui.theme.TMSuccess
import com.teenagemoney.app.ui.theme.TMWhite
import com.teenagemoney.app.ui.theme.TeenageMoneyTheme
import com.teenagemoney.app.widget.TeenageMoneyWidget
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A lightweight overlay activity that provides direct transaction entry from
 * the home-screen widget without navigating into the full application.
 *
 * Supports:
 * - Expense and Income toggling
 * - Numeric keypad input
 * - Category selection (Food, Transport, Shopping, Education, Entertainment, Others, etc.)
 * - Optional description
 * - Destination space selection for income
 * - Immediate local database update and widget sync
 * - Haptic feedback and smooth confirmation state before dismissal
 */
class QuickExpenseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = TeenageMoneyRepository(this)
        val initialType = intent.getStringExtra("EXTRA_TX_TYPE") ?: "expense"

        setContent {
            TeenageMoneyTheme {
                val localView = LocalView.current
                var currentSpendingBalance by remember {
                    mutableDoubleStateOf(
                        repository.getSpaces().firstOrNull { it.name == "Spending" }?.balance ?: 0.0
                    )
                }
                val spaces = remember { repository.getSpaces() }
                val profile = remember { repository.getUserProfile() }
                val customCategories = remember { repository.getCategories() }

                QuickTransactionSheet(
                    initialType = initialType,
                    spendingBalance = currentSpendingBalance,
                    spaces = spaces.map { it.name },
                    expenseCategories = customCategories,
                    userInitial = profile?.name?.trim()?.firstOrNull()?.uppercase() ?: "TM",
                    onDismiss = { finish() },
                    onSaveExpense = { amount, desc, category, onSuccess ->
                        runCatching {
                            val (_, newSpending) = repository.addExpense(
                                title = desc,
                                amount = amount,
                                category = category,
                                source = "Widget"
                            )
                            newSpending
                        }.onSuccess { updatedSpending ->
                            currentSpendingBalance = updatedSpending
                            localView.performHapticFeedback(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    HapticFeedbackConstants.CONFIRM
                                } else {
                                    HapticFeedbackConstants.VIRTUAL_KEY
                                }
                            )
                            TeenageMoneyWidget.refreshAll(this)
                            onSuccess(updatedSpending)
                        }.onFailure { error ->
                            localView.performHapticFeedback(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    HapticFeedbackConstants.REJECT
                                } else {
                                    HapticFeedbackConstants.LONG_PRESS
                                }
                            )
                            Toast.makeText(
                                this,
                                error.message ?: "Could not log expense",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onSaveIncome = { amount, desc, category, destSpace, onSuccess ->
                        runCatching {
                            repository.addIncome(
                                title = desc,
                                amount = amount,
                                category = category,
                                destinationSpace = destSpace,
                                source = "Widget"
                            )
                            repository.getSpaces().firstOrNull { it.name == "Spending" }?.balance ?: currentSpendingBalance
                        }.onSuccess { updatedSpending ->
                            currentSpendingBalance = updatedSpending
                            localView.performHapticFeedback(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    HapticFeedbackConstants.CONFIRM
                                } else {
                                    HapticFeedbackConstants.VIRTUAL_KEY
                                }
                            )
                            TeenageMoneyWidget.refreshAll(this)
                            onSuccess(updatedSpending)
                        }.onFailure { error ->
                            localView.performHapticFeedback(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    HapticFeedbackConstants.REJECT
                                } else {
                                    HapticFeedbackConstants.LONG_PRESS
                                }
                            )
                            Toast.makeText(
                                this,
                                error.message ?: "Could not add income",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onError = { message ->
                        localView.performHapticFeedback(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                HapticFeedbackConstants.REJECT
                            } else {
                                HapticFeedbackConstants.LONG_PRESS
                            }
                        )
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun QuickTransactionSheet(
    initialType: String = "expense",
    spendingBalance: Double,
    spaces: List<String>,
    expenseCategories: List<String>,
    userInitial: String,
    onDismiss: () -> Unit,
    onSaveExpense: (amount: Double, desc: String, category: String, onSuccess: (Double) -> Unit) -> Unit,
    onSaveIncome: (amount: Double, desc: String, category: String, destinationSpace: String, onSuccess: (Double) -> Unit) -> Unit,
    onError: (String) -> Unit
) {
    var txType by remember { mutableStateOf(if (initialType.equals("income", ignoreCase = true)) "income" else "expense") }
    var amountText by remember { mutableStateOf("0") }
    var description by remember { mutableStateOf("") }

    val defaultExpenseCategories = remember(expenseCategories) {
        val standard = listOf("Food", "Transport", "Shopping", "Education", "Entertainment", "Others")
        (standard + expenseCategories).distinct()
    }
    val defaultIncomeCategories = remember {
        listOf("Allowance", "Salary", "Freelance", "Gift", "Others")
    }

    var selectedExpenseCategory by remember { mutableStateOf("Food") }
    var selectedIncomeCategory by remember { mutableStateOf("Allowance") }
    var selectedDestinationSpace by remember { mutableStateOf(spaces.firstOrNull { it == "Spending" } ?: spaces.firstOrNull() ?: "Spending") }

    // Confirmation animation state
    var isConfirmed by remember { mutableStateOf(false) }
    var confirmationSummary by remember { mutableStateOf("") }
    var confirmedNewBalance by remember { mutableDoubleStateOf(spendingBalance) }

    val localView = LocalView.current
    val now = remember { LocalDateTime.now() }
    val formattedDate = remember { now.format(DateTimeFormatter.ofPattern("EEE, d MMM · h:mm a", Locale.ENGLISH)) }

    LaunchedEffect(isConfirmed) {
        if (isConfirmed) {
            delay(750)
            onDismiss()
        }
    }

    fun appendKey(value: String) {
        localView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        amountText = when {
            value == "del" && amountText.length > 1 -> amountText.dropLast(1)
            value == "del" -> "0"
            value == "." && amountText.contains(".") -> amountText
            value == "." && amountText.isEmpty() -> "0."
            amountText == "0" && value != "." -> value
            amountText.contains(".") && amountText.substringAfter('.').length >= 2 -> amountText
            amountText.length >= 8 -> amountText
            else -> amountText + value
        }
    }

    BottomSheetWrapper(onDismiss = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            if (isConfirmed) {
                // Subtle confirmation animation state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(if (txType == "expense") TMCoralLight else TMMintBg)
                            .border(
                                2.dp,
                                if (txType == "expense") TMCoral else TMSuccess,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = if (txType == "expense") TMCoral else TMSuccess,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (txType == "expense") "Expense Logged" else "Income Added",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMInk
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = confirmationSummary,
                        fontSize = 14.sp,
                        color = TMMuted,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Spending balance: ${TeenageMoneyRepository.formatMoney(confirmedNewBalance)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TMInk
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag indicator pill
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TMLine)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Top Bar: Dismiss, Eyebrow & User Initials
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TMInk)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "QUICK ENTRY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TMMuted,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = formattedDate,
                                fontSize = 12.sp,
                                color = TMInk,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TMCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userInitial,
                                color = TMWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Segmented Control: Expense vs Income
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF3F2F0))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isExpense = txType == "expense"
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (isExpense) TMCoral else Color.Transparent)
                                .clickable {
                                    txType = "expense"
                                    localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⬈ Expense",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isExpense) TMWhite else TMMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (!isExpense) TMSuccess else Color.Transparent)
                                .clickable {
                                    txType = "income"
                                    localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⬊ Income",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isExpense) TMWhite else TMMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Context indicator (Money Left or Destination Space)
                    if (txType == "expense") {
                        Text(
                            text = "Money left in Spending: ${TeenageMoneyRepository.formatMoney(spendingBalance)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (spendingBalance > 0) TMInk else TMCoral
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Deposit to: ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TMMuted
                            )
                            spaces.forEach { spaceName ->
                                val isSelected = selectedDestinationSpace == spaceName
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 3.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) TMMintBg else Color.Transparent)
                                        .border(
                                            1.dp,
                                            if (isSelected) TMMintBorder else TMLine,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedDestinationSpace = spaceName }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = spaceName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TMMintText else TMInk
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Hero Amount Display
                    Text(
                        text = "₹$amountText",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (txType == "expense") TMCoral else TMSuccess
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Selection Chips
                    val currentCategories = if (txType == "expense") defaultExpenseCategories else defaultIncomeCategories
                    val activeSelectedCategory = if (txType == "expense") selectedExpenseCategory else selectedIncomeCategory

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentCategories.forEach { category ->
                            val isSelected = category == activeSelectedCategory
                            val icon = when (category.lowercase(Locale.ENGLISH)) {
                                "food" -> "☕"
                                "transport" -> "⚡"
                                "shopping" -> "🛍"
                                "education" -> "✎"
                                "entertainment" -> "★"
                                "allowance" -> "🎁"
                                "salary" -> "💼"
                                "freelance" -> "💻"
                                "gift" -> "✨"
                                else -> "✦"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when {
                                            isSelected && txType == "expense" -> TMCoralLight
                                            isSelected -> TMMintBg
                                            else -> Color(0xFFF7F7F8)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            isSelected && txType == "expense" -> TMCoral
                                            isSelected -> TMMintBorder
                                            else -> TMLine
                                        },
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                        if (txType == "expense") selectedExpenseCategory = category
                                        else selectedIncomeCategory = category
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = icon, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = when {
                                            isSelected && txType == "expense" -> TMCoral
                                            isSelected -> TMMintText
                                            else -> TMInk
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description Input
                    SheetInputField(
                        value = description,
                        onValueChange = { if (it.length <= 60) description = it },
                        placeholder = if (txType == "expense") "What was it for? (optional)" else "Source note (optional)"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4-row Numeric Keypad
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf(".", "0", "del")
                    )

                    keys.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                Button(
                                    onClick = { appendKey(key) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFECEBEA),
                                        contentColor = TMInk
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(42.dp)
                                ) {
                                    Text(
                                        text = if (key == "del") "⌫" else key,
                                        fontSize = if (key == "del") 18.sp else 19.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Clear Save Action
                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull()
                            when {
                                amount == null || amount <= 0.0 -> onError("Please enter a valid amount")
                                txType == "expense" && amount > spendingBalance -> {
                                    onError("Not enough in Spending. You have ${TeenageMoneyRepository.formatMoney(spendingBalance)}.")
                                }
                                txType == "expense" -> {
                                    onSaveExpense(
                                        amount,
                                        description.trim().ifBlank { selectedExpenseCategory },
                                        selectedExpenseCategory
                                    ) { newSpending ->
                                        confirmedNewBalance = newSpending
                                        confirmationSummary = "Logged ${TeenageMoneyRepository.formatMoney(amount)} for ${selectedExpenseCategory}"
                                        isConfirmed = true
                                    }
                                }
                                else -> {
                                    onSaveIncome(
                                        amount,
                                        description.trim().ifBlank { selectedIncomeCategory },
                                        selectedIncomeCategory,
                                        selectedDestinationSpace
                                    ) { newSpending ->
                                        confirmedNewBalance = newSpending
                                        confirmationSummary = "Added ${TeenageMoneyRepository.formatMoney(amount)} to $selectedDestinationSpace"
                                        isConfirmed = true
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (txType == "expense") TMInk else TMSuccess,
                            contentColor = TMWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (txType == "expense") "Log Expense →" else "Add Income →",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
