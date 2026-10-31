package com.teenagemoney.app.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.UserProfile
import com.teenagemoney.app.ui.theme.TMBackground
import com.teenagemoney.app.ui.theme.TMCoral
import com.teenagemoney.app.ui.theme.TMInk
import com.teenagemoney.app.ui.theme.TMLine
import com.teenagemoney.app.ui.theme.TMMintBg
import com.teenagemoney.app.ui.theme.TMMintBorder
import com.teenagemoney.app.ui.theme.TMMintText
import com.teenagemoney.app.ui.theme.TMMuted
import com.teenagemoney.app.ui.theme.TMWhite
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class OnboardingStep {
    data object NameInput : OnboardingStep()
    data class GreetingTransition(val name: String) : OnboardingStep()
    data class DobInput(val name: String) : OnboardingStep()
    data class AgeTransition(val name: String, val dob: String, val age: Int) : OnboardingStep()
    data class StatusInput(val name: String, val dob: String, val age: Int) : OnboardingStep()
    data class WelcomeTransition(val name: String, val dob: String, val age: Int, val status: String) : OnboardingStep()
}

@Composable
fun OnboardingScreen(
    repository: TeenageMoneyRepository,
    onComplete: (UserProfile) -> Unit
) {
    var step by remember { mutableStateOf<OnboardingStep>(OnboardingStep.NameInput) }
    var inputName by remember { mutableStateOf("") }
    var selectedDob by remember { mutableStateOf("") }
    var calculatedAge by remember { mutableIntStateOf(0) }
    val selectedStatus = remember { mutableStateListOf<String>() }

    val context = LocalContext.current

    AnimatedContent(
        targetState = step,
        transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(250)) },
        label = "onboarding_step"
    ) { currentStep ->
        when (currentStep) {
            is OnboardingStep.NameInput -> {
                OnboardingQuestionLayout(
                    stepText = "01 / 03",
                    progress = 0.33f,
                    eyebrow = "WELCOME IN",
                    title = "What should we call you?",
                    subtitle = "A name makes this feel a little more yours.",
                    content = {
                        Column {
                            Text(
                                text = "Your name",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TMMuted,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TMWhite, RoundedCornerShape(14.dp))
                                    .border(1.dp, TMLine, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                if (inputName.isEmpty()) {
                                    Text(
                                        text = "Your name",
                                        color = TMMuted,
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                }
                                BasicTextField(
                                    value = inputName,
                                    onValueChange = { if (it.length <= 40) inputName = it },
                                    singleLine = true,
                                    textStyle = TextStyle(
                                        color = TMInk,
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    cursorBrush = SolidColor(TMCoral),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            val clean = inputName.trim().replace("\\s+".toRegex(), " ")
                                            if (clean.length >= 2) {
                                                inputName = clean
                                                step = OnboardingStep.GreetingTransition(clean)
                                            }
                                        }
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    },
                    primaryButtonText = "Continue →",
                    isPrimaryEnabled = inputName.trim().length >= 2,
                    onPrimaryClick = {
                        val clean = inputName.trim().replace("\\s+".toRegex(), " ")
                        if (clean.length >= 2) {
                            inputName = clean
                            step = OnboardingStep.GreetingTransition(clean)
                        }
                    }
                )
            }

            is OnboardingStep.GreetingTransition -> {
                AutoAdvanceTransition(
                    primaryText = "Hello ${currentStep.name} 👋",
                    secondaryText = "",
                    tapHint = "Tap to continue",
                    timeoutMs = 3800L,
                    onAdvance = {
                        step = OnboardingStep.DobInput(currentStep.name)
                    }
                )
            }

            is OnboardingStep.DobInput -> {
                val now = LocalDate.now()
                val datePickerDialog = remember {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val picked = LocalDate.of(year, month + 1, dayOfMonth)
                            val today = LocalDate.now()
                            if (!picked.isAfter(today)) {
                                val iso = picked.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                selectedDob = iso
                                calculatedAge = Period.between(picked, today).years
                            }
                        },
                        now.year - 18,
                        now.monthValue - 1,
                        now.dayOfMonth
                    ).apply {
                        datePicker.maxDate = System.currentTimeMillis()
                    }
                }

                val formattedDisplayDate = remember(selectedDob) {
                    if (selectedDob.isNotEmpty()) {
                        try {
                            val parsed = LocalDate.parse(selectedDob)
                            parsed.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH))
                        } catch (e: Exception) {
                            selectedDob
                        }
                    } else {
                        ""
                    }
                }

                OnboardingQuestionLayout(
                    stepText = "02 / 03",
                    progress = 0.66f,
                    eyebrow = "JUST ONE MORE THING",
                    title = "When were you born?",
                    subtitle = "We’ll use it to keep your profile and age up to date.",
                    content = {
                        Column {
                            Text(
                                text = "Date of birth",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TMMuted,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TMWhite, RoundedCornerShape(14.dp))
                                    .border(1.dp, TMLine, RoundedCornerShape(14.dp))
                                    .clickable { datePickerDialog.show() }
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (formattedDisplayDate.isNotEmpty()) formattedDisplayDate else "Select your birth date",
                                        color = if (formattedDisplayDate.isNotEmpty()) TMInk else TMMuted,
                                        fontSize = 16.sp,
                                        fontWeight = if (formattedDisplayDate.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "📅",
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            if (selectedDob.isNotEmpty() && calculatedAge >= 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Calculated age: $calculatedAge years old",
                                    fontSize = 13.sp,
                                    color = TMCoral,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    },
                    primaryButtonText = "Continue →",
                    isPrimaryEnabled = selectedDob.isNotEmpty(),
                    onPrimaryClick = {
                        if (selectedDob.isNotEmpty()) {
                            step = OnboardingStep.AgeTransition(currentStep.name, selectedDob, calculatedAge)
                        }
                    }
                )
            }

            is OnboardingStep.AgeTransition -> {
                AutoAdvanceTransition(
                    primaryText = "So, you’re ${currentStep.age}.",
                    secondaryText = "That’s a great time to start taking control of your money.",
                    tapHint = "Tap to continue",
                    timeoutMs = 3800L,
                    onAdvance = {
                        step = OnboardingStep.StatusInput(currentStep.name, currentStep.dob, currentStep.age)
                    }
                )
            }

            is OnboardingStep.StatusInput -> {
                OnboardingQuestionLayout(
                    stepText = "03 / 03",
                    progress = 1.0f,
                    eyebrow = "A LITTLE CONTEXT",
                    title = "What best describes you right now?",
                    subtitle = "Choose both if they both fit.",
                    content = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatusOptionCard(
                                icon = "🎓",
                                title = "Student",
                                isSelected = selectedStatus.contains("Student"),
                                onClick = {
                                    if (selectedStatus.contains("Student")) {
                                        selectedStatus.remove("Student")
                                    } else {
                                        selectedStatus.add("Student")
                                    }
                                }
                            )
                            StatusOptionCard(
                                icon = "💼",
                                title = "Working Professional",
                                isSelected = selectedStatus.contains("Working Professional"),
                                onClick = {
                                    if (selectedStatus.contains("Working Professional")) {
                                        selectedStatus.remove("Working Professional")
                                    } else {
                                        selectedStatus.add("Working Professional")
                                    }
                                }
                            )
                        }
                    },
                    primaryButtonText = "Continue →",
                    isPrimaryEnabled = true,
                    onPrimaryClick = {
                        val statusStr = selectedStatus.joinToString(", ")
                        step = OnboardingStep.WelcomeTransition(
                            currentStep.name,
                            currentStep.dob,
                            currentStep.age,
                            statusStr
                        )
                    },
                    secondaryButtonText = "Skip for now",
                    onSecondaryClick = {
                        selectedStatus.clear()
                        step = OnboardingStep.WelcomeTransition(
                            currentStep.name,
                            currentStep.dob,
                            currentStep.age,
                            "" // Empty string for skipped (unanswered/unknown)
                        )
                    }
                )
            }

            is OnboardingStep.WelcomeTransition -> {
                AutoAdvanceTransition(
                    primaryText = "You’re all set, ${currentStep.name}! 🎉",
                    secondaryText = "Let’s make your money work for you.",
                    tapHint = "Tap to enter",
                    timeoutMs = 3200L,
                    onAdvance = {
                        // Persist profile and mark onboarding as complete
                        val profile = UserProfile(
                            name = currentStep.name,
                            dateOfBirth = currentStep.dob,
                            age = currentStep.age,
                            status = currentStep.status,
                            onboardingCompleted = true
                        )
                        repository.saveUserProfile(profile)
                        repository.setOnboardingCompleted(true)
                        onComplete(profile)
                    }
                )
            }
        }
    }
}

@Composable
fun OnboardingQuestionLayout(
    stepText: String,
    progress: Float,
    eyebrow: String,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
    primaryButtonText: String,
    isPrimaryEnabled: Boolean = true,
    onPrimaryClick: () -> Unit,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Top step and progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stepText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMMuted
                    )
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(TMLine)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(TMCoral)
                        )
                    }
                }

                // Question content
                Text(
                    text = eyebrow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.2.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TMInk,
                    lineHeight = 34.sp,
                    letterSpacing = (-0.8).sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = TMMuted,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(bottom = 28.dp)
                )

                content()
            }

            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Button(
                    onClick = onPrimaryClick,
                    enabled = isPrimaryEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TMCoral,
                        disabledContainerColor = TMCoral.copy(alpha = 0.4f),
                        contentColor = TMWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(
                        text = primaryButtonText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (secondaryButtonText != null && onSecondaryClick != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = onSecondaryClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = secondaryButtonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TMMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusOptionCard(
    icon: String,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) TMMintBg else TMWhite
    val borderCol = if (isSelected) TMMintBorder else TMLine
    val textCol = if (isSelected) TMMintText else TMInk

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.5.dp, borderCol, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textCol
            )
        }
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isSelected) TMMintText else TMLine),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isSelected) "✓" else "+",
                fontSize = 14.sp,
                color = if (isSelected) TMWhite else TMMuted,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AutoAdvanceTransition(
    primaryText: String,
    secondaryText: String,
    tapHint: String,
    timeoutMs: Long,
    onAdvance: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(timeoutMs)
        onAdvance()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onAdvance() }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = primaryText,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TMInk,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                letterSpacing = (-1.0).sp
            )
            if (secondaryText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = secondaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TMMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        Text(
            text = tapHint,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TMMuted,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
