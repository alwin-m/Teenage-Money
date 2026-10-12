package com.teenagemoney.app.ui

import android.content.Context
import android.provider.Settings
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.MoneySpace
import com.teenagemoney.app.data.SavingsGoal
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.data.UserProfile
import com.teenagemoney.app.BuildConfig
import com.teenagemoney.app.ui.components.AddExpenseSheet
import com.teenagemoney.app.ui.components.AddCategorySheet
import com.teenagemoney.app.ui.components.AddGoalSheet
import com.teenagemoney.app.ui.components.AddMoneySheet
import com.teenagemoney.app.ui.components.AddSpaceSheet
import com.teenagemoney.app.ui.components.AdjustBalanceSheet
import com.teenagemoney.app.ui.components.GoalContributionSheet
import com.teenagemoney.app.ui.components.BottomNavBar
import com.teenagemoney.app.ui.components.ReceiptDialog
import com.teenagemoney.app.ui.screens.ActivityScreen
import com.teenagemoney.app.ui.screens.GoalsScreen
import com.teenagemoney.app.ui.screens.HomeScreen
import com.teenagemoney.app.ui.screens.ProfileScreen
import com.teenagemoney.app.ui.screens.OnboardingScreen
import com.teenagemoney.app.ui.screens.SplashScreen
import com.teenagemoney.app.ui.audio.ReceiptFeedbackPlayer
import com.teenagemoney.app.ui.theme.TMBackground
import com.teenagemoney.app.ui.theme.TMWhite
import kotlinx.coroutines.delay

enum class AppDestination {
    SPLASH,
    ONBOARDING,
    MAIN
}

@Composable
fun TeenageMoneyApp(repository: TeenageMoneyRepository) {
    val context = LocalContext.current
    val reduceMotion = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f
        ) == 0f
    }

    var currentDestination by remember { mutableStateOf(AppDestination.SPLASH) }
    var activeTab by remember { mutableStateOf("Home") }

    var userProfile by remember { mutableStateOf<UserProfile?>(repository.getUserProfile()) }
    var spaces by remember { mutableStateOf<List<MoneySpace>>(repository.getSpaces()) }
    var transactions by remember { mutableStateOf<List<TransactionItem>>(repository.getTransactions()) }
    var goals by remember { mutableStateOf<List<SavingsGoal>>(repository.getGoals()) }

    var showAddExpense by remember { mutableStateOf(false) }
    var showAddMoney by remember { mutableStateOf(false) }
    var showAddGoal by remember { mutableStateOf(false) }
    var showAddSpace by remember { mutableStateOf(false) }
    var showAddCategory by remember { mutableStateOf(false) }
    var editingSpace by remember { mutableStateOf<MoneySpace?>(null) }
    var contributingGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var categories by remember { mutableStateOf(repository.getCategories()) }
    var soundEnabled by remember { mutableStateOf(repository.isSoundEnabled()) }
    var amountsMasked by remember { mutableStateOf(repository.isPrivacyMode()) }
    var activeReceipt by remember { mutableStateOf<TransactionItem?>(null) }
    var toastText by remember { mutableStateOf("") }
    val receiptFeedback = remember { ReceiptFeedbackPlayer(context) }

    DisposableEffect(receiptFeedback) {
        onDispose { receiptFeedback.release() }
    }

    fun vibrate(duration: Long = 25) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(duration)
                }
            }
        } catch (_: Exception) {}
    }

    fun showToast(msg: String) {
        toastText = msg
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(repository.exportJson().toByteArray(Charsets.UTF_8))
            } ?: error("Could not open the selected file")
            showToast("Data exported successfully")
        } catch (e: Exception) {
            showToast(e.message ?: "Could not export data")
        }
    }

    LaunchedEffect(toastText) {
        if (toastText.isNotEmpty()) {
            delay(2400)
            toastText = ""
        }
    }

    when (currentDestination) {
        AppDestination.SPLASH -> {
            SplashScreen(onDone = {
                val isOnboarded = repository.isOnboardingCompleted()
                currentDestination = if (isOnboarded) AppDestination.MAIN else AppDestination.ONBOARDING
            })
        }

        AppDestination.ONBOARDING -> {
            OnboardingScreen(
                repository = repository,
                onComplete = { profile ->
                    userProfile = profile
                    currentDestination = AppDestination.MAIN
                }
            )
        }

        AppDestination.MAIN -> {
            val displayName = userProfile?.name?.ifEmpty { "Alex" } ?: "Alex"
            val spendingSpace = spaces.find { it.name == "Spending" }
            val spendingBalance = spendingSpace?.balance ?: 0.0

            BackHandler(enabled = activeReceipt != null || showAddExpense || showAddMoney || showAddGoal || showAddSpace || showAddCategory || editingSpace != null || contributingGoal != null || activeTab != "Home") {
                when {
                    activeReceipt != null -> activeReceipt = null
                    showAddExpense -> showAddExpense = false
                    showAddMoney -> showAddMoney = false
                    showAddGoal -> showAddGoal = false
                    showAddSpace -> showAddSpace = false
                    showAddCategory -> showAddCategory = false
                    editingSpace != null -> editingSpace = null
                    contributingGoal != null -> contributingGoal = null
                    else -> activeTab = "Home"
                }
            }

            Scaffold(
                containerColor = TMBackground,
                bottomBar = {
                    BottomNavBar(
                        activeTab = activeTab,
                        onTabSelected = { tab ->
                            vibrate(10)
                            activeTab = tab
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (activeTab) {
                        "Home" -> {
                            HomeScreen(
                                userName = displayName,
                                spaces = spaces,
                                transactions = transactions,
                                goals = goals,
                                onAddMoneyClick = {
                                    vibrate(15)
                                    showAddMoney = true
                                },
                                onLogExpenseClick = {
                                    vibrate(15)
                                    showAddExpense = true
                                },
                                onSeeAllActivityClick = {
                                    vibrate(10)
                                    activeTab = "Activity"
                                },
                                onGoalsClick = {
                                    vibrate(10)
                                    activeTab = "Goals"
                                },
                                onAddGoalClick = {
                                    vibrate(15)
                                    showAddGoal = true
                                },
                                onSpaceClick = { space -> editingSpace = space },
                                onAddSpaceClick = { showAddSpace = true },
                                onTransactionClick = { tx -> activeReceipt = tx },
                                amountsMasked = amountsMasked,
                                onPrivacyToggle = {
                                    amountsMasked = !amountsMasked
                                    repository.savePrivacyMode(amountsMasked)
                                }
                            )
                        }

                        "Activity" -> {
                            ActivityScreen(
                                transactions = transactions,
                                onLogExpenseClick = {
                                    vibrate(15)
                                    showAddExpense = true
                                },
                                onTransactionClick = { tx -> activeReceipt = tx }
                            )
                        }

                        "Goals" -> {
                            GoalsScreen(
                                goals = goals,
                                onAddGoalClick = {
                                    vibrate(15)
                                    showAddGoal = true
                                },
                                onContributeClick = { goal -> contributingGoal = goal }
                            )
                        }

                        "Profile" -> {
                            ProfileScreen(
                                profile = userProfile,
                                monthlySalary = repository.getMonthlySalary(),
                                spaces = spaces,
                                transactions = transactions,
                                soundEnabled = soundEnabled,
                                appVersion = BuildConfig.VERSION_NAME,
                                buildId = BuildConfig.BUILD_ID,
                                onSaveSalary = { salary ->
                                    repository.saveMonthlySalary(salary)
                                    showToast("Monthly salary saved")
                                },
                                onSoundEnabledChange = { enabled ->
                                    soundEnabled = enabled
                                    repository.saveSoundEnabled(enabled)
                                },
                                onExport = {
                                    exportLauncher.launch("teenage-money-export.json")
                                }
                            )
                        }
                    }

                    // Toast popup indicator
                    AnimatedVisibility(
                        visible = toastText.isNotEmpty(),
                        enter = if (reduceMotion) fadeIn() else fadeIn() + slideInVertically { -it },
                        exit = if (reduceMotion) fadeOut() else fadeOut() + slideOutVertically { -it },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .shadow(8.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF23222B))
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "✓  $toastText",
                                color = TMWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Bottom Sheets
            if (showAddExpense) {
                AddExpenseSheet(
                    spendingBalance = spendingBalance,
                    categories = categories,
                    onDismiss = { showAddExpense = false },
                    onAddCategory = { showAddExpense = false; showAddCategory = true },
                    onSubmit = { amount, description, category ->
                        try {
                            val (newTx, _) = repository.addExpense(description, amount, category)
                            // Refresh local state from SQLite
                            transactions = repository.getTransactions()
                            spaces = repository.getSpaces()
                            showAddExpense = false
                            activeReceipt = newTx
                            receiptFeedback.play(soundEnabled)
                            showToast("Expense logged to Spending")
                        } catch (e: Exception) {
                            showToast(e.message ?: "Could not add expense")
                        }
                    },
                    onError = { msg ->
                        vibrate(40)
                        showToast(msg)
                    }
                )
            }

            if (showAddMoney) {
                AddMoneySheet(
                    spaces = spaces,
                    onDismiss = { showAddMoney = false },
                    onAddSpace = { showAddMoney = false; showAddSpace = true },
                    onSubmit = { amount, destination ->
                        try {
                            repository.addMoney(amount, destination)
                            vibrate(25)
                            spaces = repository.getSpaces()
                            transactions = repository.getTransactions()
                            showAddMoney = false
                            showToast(
                                if (amountsMasked) "Money added to $destination"
                                else "${TeenageMoneyRepository.formatMoney(amount)} added to $destination"
                            )
                        } catch (e: Exception) {
                            showToast(e.message ?: "Could not add money")
                        }
                    },
                    onError = { msg ->
                        vibrate(40)
                        showToast(msg)
                    }
                )
            }

            if (showAddGoal) {
                AddGoalSheet(
                    onDismiss = { showAddGoal = false },
                    onSubmit = { title, target, saved, tint, emoji ->
                        try {
                            repository.addGoal(title, target, saved, tint, emoji)
                            vibrate(20)
                            goals = repository.getGoals()
                            showAddGoal = false
                            showToast("Goal \"$title\" created!")
                        } catch (e: Exception) {
                            showToast(e.message ?: "Could not create goal")
                        }
                    },
                    onError = { msg ->
                        vibrate(40)
                        showToast(msg)
                    }
                )
            }

            if (showAddSpace) {
                AddSpaceSheet(
                    onDismiss = { showAddSpace = false },
                    onSubmit = { name, note ->
                        try {
                            repository.addSpace(name, note)
                            spaces = repository.getSpaces()
                            showAddSpace = false
                            showToast("$name added")
                        } catch (e: Exception) { showToast(e.message ?: "Could not add space") }
                    },
                    onError = { msg -> showToast(msg) }
                )
            }

            editingSpace?.let { space ->
                AdjustBalanceSheet(
                    space = space,
                    onDismiss = { editingSpace = null },
                    onSubmit = { newBalance ->
                        repository.correctSpaceBalance(space.name, newBalance)
                        spaces = repository.getSpaces()
                        editingSpace = null
                        showToast("${space.name} balance updated")
                    },
                    onError = { msg -> showToast(msg) }
                )
            }

            contributingGoal?.let { goal ->
                GoalContributionSheet(
                    goal = goal,
                    savingsBalance = spaces.firstOrNull { it.name == "Savings" }?.balance ?: 0.0,
                    onDismiss = { contributingGoal = null },
                    onSubmit = { amount ->
                        try {
                            repository.contributeToGoal(goal.id, amount)
                            spaces = repository.getSpaces()
                            transactions = repository.getTransactions()
                            goals = repository.getGoals()
                            contributingGoal = null
                            showToast("Money moved to ${goal.title}")
                        } catch (e: Exception) { showToast(e.message ?: "Could not add to goal") }
                    },
                    onError = { msg -> showToast(msg) }
                )
            }

            if (showAddCategory) {
                AddCategorySheet(
                    onDismiss = { showAddCategory = false },
                    onSubmit = { name ->
                        try {
                            categories = repository.addCategory(name)
                            showAddCategory = false
                            showAddExpense = true
                            showToast("$name added to categories")
                        } catch (e: Exception) { showToast(e.message ?: "Could not add category") }
                    },
                    onError = { msg -> showToast(msg) }
                )
            }

            // Thermal Receipt
            activeReceipt?.let { tx ->
                ReceiptDialog(
                    transaction = tx,
                    onDismiss = { activeReceipt = null }
                )
            }
        }
    }
}
