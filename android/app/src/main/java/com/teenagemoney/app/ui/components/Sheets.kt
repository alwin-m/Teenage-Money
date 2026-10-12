package com.teenagemoney.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.SavingsGoal
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.ui.theme.TMCardDark
import com.teenagemoney.app.ui.theme.TMCoral
import com.teenagemoney.app.ui.theme.TMInk
import com.teenagemoney.app.ui.theme.TMLine
import com.teenagemoney.app.ui.theme.TMMintBg
import com.teenagemoney.app.ui.theme.TMMintBorder
import com.teenagemoney.app.ui.theme.TMMintText
import com.teenagemoney.app.ui.theme.TMMuted
import com.teenagemoney.app.ui.theme.TMPeachBg
import com.teenagemoney.app.ui.theme.TMPeachBorder
import com.teenagemoney.app.ui.theme.TMPeachText
import com.teenagemoney.app.ui.theme.TMWhite

@Composable
fun AddExpenseSheet(
    spendingBalance: Double,
    categories: List<String>,
    onDismiss: () -> Unit,
    onAddCategory: () -> Unit,
    onSubmit: (amount: Double, description: String, category: String) -> Unit,
    onError: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food") }

    val visibleCategories = categories + "+ Add category"

    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            SheetHeader(
                eyebrow = "QUICK LOG",
                title = "What did you spend?",
                onClose = onDismiss
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Amount input
            Text(
                text = "Amount",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TMMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            SheetInputField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                placeholder = "₹ 0",
                keyboardType = KeyboardType.Decimal
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description input
            Text(
                text = "What was it?",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TMMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            SheetInputField(
                value = descText,
                onValueChange = { descText = it },
                placeholder = "e.g. Coffee, Subway, Book"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category chips
            Text(
                text = "Category",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TMMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            visibleCategories.chunked(3).forEachIndexed { rowIndex, row ->
                if (rowIndex > 0) Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { cat ->
                    CategoryChip(
                        name = cat,
                        isSelected = selectedCategory == cat && cat != "+ Add category",
                        onClick = { if (cat == "+ Add category") onAddCategory() else selectedCategory = cat },
                        modifier = Modifier.weight(1f)
                    )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        onError("Please enter a valid amount")
                        return@Button
                    }
                    if (descText.trim().isEmpty()) {
                        onError("Please enter what you spent on")
                        return@Button
                    }
                    if (amount > spendingBalance) {
                        onError("Not enough in Spending. You have ₹${spendingBalance.toInt()}.")
                        return@Button
                    }
                    onSubmit(amount, descText.trim(), selectedCategory)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Add expense →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMWhite
                )
            }
        }
    }
}

@Composable
fun AddCategorySheet(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    onError: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            SheetHeader(eyebrow = "PERSONAL CATEGORY", title = "Name this kind of spend", onClose = onDismiss)
            Spacer(modifier = Modifier.height(18.dp))
            SheetInputField(value = name, onValueChange = { name = it }, placeholder = "e.g. Music lessons")
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { if (name.trim().length < 2) onError("Category must be at least 2 characters") else onSubmit(name.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Save category →", color = TMWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun AddMoneySheet(
    spaces: List<MoneySpace>,
    onDismiss: () -> Unit,
    onAddSpace: () -> Unit,
    onSubmit: (amount: Double, destination: String) -> Unit,
    onError: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var selectedSpace by remember { mutableStateOf("Spending") }

    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            SheetHeader(
                eyebrow = "ADD MONEY",
                title = "Where should it go?",
                onClose = onDismiss
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Amount received",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TMMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            SheetInputField(
                value = amountText,
                onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                placeholder = "₹ 0",
                keyboardType = KeyboardType.Decimal
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Money Space",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TMMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                spaces.forEach { s ->
                    val isSelected = selectedSpace == s.name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TMPeachBg else TMWhite)
                            .border(1.dp, if (isSelected) TMPeachBorder else TMLine, RoundedCornerShape(12.dp))
                            .clickable { selectedSpace = s.name }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = s.icon, fontSize = 16.sp, modifier = Modifier.padding(end = 10.dp))
                            Column {
                                Text(
                                    text = s.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TMPeachText else TMInk
                                )
                                Text(text = s.note, fontSize = 11.sp, color = TMMuted)
                            }
                        }
                        if (isSelected) {
                            Text(text = "✓", color = TMPeachText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, TMLine, RoundedCornerShape(12.dp))
                        .clickable { onAddSpace() }
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+ Create a money space", color = TMInk, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        onError("Enter an amount greater than zero")
                        return@Button
                    }
                    onSubmit(amount, selectedSpace)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Add money →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMWhite
                )
            }
        }
    }
}

@Composable
fun AddGoalSheet(
    onDismiss: () -> Unit,
    onSubmit: (title: String, target: Double, saved: Double, tint: String, emoji: String) -> Unit,
    onError: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var savedText by remember { mutableStateOf("") }
    var selectedTint by remember { mutableStateOf("peach") }
    var selectedEmoji by remember { mutableStateOf("♫") }

    val emojis = listOf("♫", "◒", "★", "💻", "👟")

    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            SheetHeader(
                eyebrow = "NEW SAVINGS GOAL",
                title = "What are you saving for?",
                onClose = onDismiss
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Goal title", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted, modifier = Modifier.padding(bottom = 6.dp))
            SheetInputField(value = title, onValueChange = { title = it }, placeholder = "e.g. Mechanical Keyboard")

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Target amount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted, modifier = Modifier.padding(bottom = 6.dp))
            SheetInputField(value = targetText, onValueChange = { targetText = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "₹ 5000", keyboardType = KeyboardType.Decimal)

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Already saved (optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted, modifier = Modifier.padding(bottom = 6.dp))
            SheetInputField(value = savedText, onValueChange = { savedText = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "₹ 0", keyboardType = KeyboardType.Decimal)

            Spacer(modifier = Modifier.height(14.dp))

            Text(text = "Icon Symbol", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted, modifier = Modifier.padding(bottom = 6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                emojis.forEach { e ->
                    val isSelected = selectedEmoji == e
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) TMPeachBg else TMWhite)
                            .border(1.dp, if (isSelected) TMCoral else TMLine, CircleShape)
                            .clickable { selectedEmoji = e },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = e, fontSize = 18.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val target = targetText.toDoubleOrNull()
                    if (title.trim().isEmpty() || target == null || target <= 0) {
                        onError("Enter a goal title and target amount")
                        return@Button
                    }
                    val saved = savedText.toDoubleOrNull() ?: 0.0
                    onSubmit(title.trim(), target, saved, selectedTint, selectedEmoji)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = "Create goal →",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMWhite
                )
            }
        }
    }
}

@Composable
fun GoalContributionSheet(
    goal: SavingsGoal,
    savingsBalance: Double,
    onDismiss: () -> Unit,
    onSubmit: (Double) -> Unit,
    onError: (String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            SheetHeader(eyebrow = "REAL SAVING", title = "Add to ${goal.title}", onClose = onDismiss)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Available in Savings: ${TeenageMoneyRepository.formatMoney(savingsBalance)}", fontSize = 13.sp, color = TMMuted)
            Spacer(modifier = Modifier.height(16.dp))
            SheetInputField(value = amountText, onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "₹ 0", keyboardType = KeyboardType.Decimal)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                val value = amountText.toDoubleOrNull()
                if (value == null || value <= 0) onError("Enter a valid contribution") else onSubmit(value)
            }, colors = ButtonDefaults.buttonColors(containerColor = TMCoral), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Move to goal →", color = TMWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SheetHeader(
    eyebrow: String,
    title: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = eyebrow,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp,
                color = TMMuted
            )
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TMInk
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(TMLine)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", fontSize = 14.sp, color = TMMuted, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SheetInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TMWhite, RoundedCornerShape(14.dp))
            .border(1.dp, TMLine, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = TMMuted, fontSize = 16.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = TMInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif
            ),
            cursorBrush = SolidColor(TMCoral),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) TMCoral else TMWhite)
            .border(1.dp, if (isSelected) TMCoral else TMLine, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) TMWhite else TMInk
        )
    }
}

@Composable
fun BottomSheetWrapper(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFFCFBFA))
                .imePadding()
                .navigationBarsPadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
        ) {
            content()
        }
    }
}

@Composable
fun AddSpaceSheet(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit,
    onError: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            SheetHeader(eyebrow = "NEW SPACE", title = "Where should it live?", onClose = onDismiss)
            Spacer(modifier = Modifier.height(18.dp))
            Text("Space name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted)
            Spacer(modifier = Modifier.height(6.dp))
            SheetInputField(value = name, onValueChange = { name = it }, placeholder = "e.g. Education")
            Spacer(modifier = Modifier.height(14.dp))
            Text("Short note (optional)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted)
            Spacer(modifier = Modifier.height(6.dp))
            SheetInputField(value = note, onValueChange = { note = it }, placeholder = "What is it for?")
            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = { if (name.trim().isEmpty()) onError("Enter a space name") else onSubmit(name.trim(), note.trim().ifEmpty { "Custom money space" }) },
                colors = ButtonDefaults.buttonColors(containerColor = TMCardDark),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Create space", color = TMWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun EditProfileSheet(
    currentName: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit,
    onError: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            SheetHeader(eyebrow = "PROFILE", title = "What should we call you?", onClose = onDismiss)
            Spacer(modifier = Modifier.height(18.dp))
            Text("Display name", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted)
            Spacer(modifier = Modifier.height(6.dp))
            SheetInputField(value = name, onValueChange = { name = it }, placeholder = "Your name")
            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = {
                    val cleanName = name.trim()
                    if (cleanName.isEmpty()) onError("Enter a name") else onSubmit(cleanName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Save profile", color = TMWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
fun AdjustBalanceSheet(
    space: MoneySpace,
    onDismiss: () -> Unit,
    onSubmit: (Double) -> Unit,
    onError: (String) -> Unit
) {
    var amountText by remember { mutableStateOf(space.balance.toString().removeSuffix(".0")) }
    var confirm by remember { mutableStateOf(false) }
    BottomSheetWrapper(onDismiss = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            SheetHeader(eyebrow = "EDIT BALANCE", title = "Change ${space.name}?", onClose = onDismiss)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Current balance: ${TeenageMoneyRepository.formatMoney(space.balance)}", fontSize = 13.sp, color = TMMuted)
            Spacer(modifier = Modifier.height(16.dp))
            Text("New balance", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted)
            Spacer(modifier = Modifier.height(6.dp))
            SheetInputField(value = amountText, onValueChange = { amountText = it.filter { ch -> ch.isDigit() || ch == '.' } }, placeholder = "₹ 0", keyboardType = KeyboardType.Decimal)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount < 0) onError("Enter zero or a positive amount") else confirm = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = TMCoral), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Change balance →", color = TMWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            if (confirm) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Are you sure? This changes the balance for ${space.name}.", color = TMInk, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { confirm = false }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = TMLine)) { Text("Keep editing", color = TMInk) }
                    Button(onClick = { onSubmit(amountText.toDouble()) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = TMCoral)) { Text("Yes, change", color = TMWhite) }
                }
            }
        }
    }
}
