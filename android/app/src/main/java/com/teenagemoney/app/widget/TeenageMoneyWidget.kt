package com.teenagemoney.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import com.teenagemoney.app.MainActivity
import com.teenagemoney.app.R
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.ui.QuickExpenseActivity

/**
 * Responsive, launcher-friendly overview of the Teenage Money financial state.
 *
 * Supports three responsive sizes:
 * - Compact: Balance, financial status dot, date, and quick-add action.
 * - Medium: Balance, weekly income, weekly expenses, status, and quick-add action.
 * - Expanded: Full financial overview with weekly metrics, recent transactions,
 *   financial position indicator, and quick-add action.
 *
 * Uses native RemoteViews for zero overhead, instant rendering, and broad launcher
 * compatibility from Android 8.0 (API 26) through Android 15+ (API 36).
 */
class TeenageMoneyWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { appWidgetId -> updateAppWidget(context, manager, appWidgetId) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: android.os.Bundle
    ) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    companion object {
        fun refreshAll(context: Context) {
            val appContext = context.applicationContext
            val manager = AppWidgetManager.getInstance(appContext)
            val component = ComponentName(appContext, TeenageMoneyWidget::class.java)
            manager.getAppWidgetIds(component).forEach { appWidgetId ->
                updateAppWidget(appContext, manager, appWidgetId)
            }
        }

        private fun updateAppWidget(
            context: Context,
            manager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val repository = TeenageMoneyRepository(context)
            val spaces = repository.getSpaces()
            val spendingBalance = spaces.firstOrNull { it.name == "Spending" }?.balance ?: 0.0
            val transactions = repository.getTransactions()
            val privacyMode = repository.isPrivacyMode()

            val weekIncome = TeenageMoneyRepository.calcIncomeThisWeek(transactions)
            val weekExpenses = TeenageMoneyRepository.calcSpentThisWeek(transactions)
            val financialPosition = TeenageMoneyRepository.evaluateFinancialPosition(spendingBalance, weekExpenses)
            val dateText = TeenageMoneyRepository.widgetDateText()

            val balanceDisplay = TeenageMoneyRepository.displayMoney(spendingBalance, privacyMode)
            val incomeDisplay = if (privacyMode) "₹ ••••" else "+${TeenageMoneyRepository.formatMoney(weekIncome)}"
            val expensesDisplay = if (privacyMode) "₹ ••••" else "−${TeenageMoneyRepository.formatMoney(weekExpenses)}"
            val statusColor = runCatching { Color.parseColor(financialPosition.colorHex) }.getOrDefault(Color.parseColor("#1B9E5A"))

            val openAppIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val quickTxIntent = PendingIntent.getActivity(
                context,
                appWidgetId + 10000,
                Intent(context, QuickExpenseActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 1. Compact RemoteViews
            val compactViews = RemoteViews(context.packageName, R.layout.widget_compact).apply {
                setTextViewText(R.id.widget_compact_date, dateText)
                setTextViewText(R.id.widget_compact_balance, balanceDisplay)
                setTextViewText(R.id.widget_compact_status, financialPosition.label)
                setTextColor(R.id.widget_compact_status, statusColor)
                setOnClickPendingIntent(R.id.widget_compact_root, openAppIntent)
                setOnClickPendingIntent(R.id.widget_compact_add, quickTxIntent)
            }

            // 2. Medium RemoteViews
            val mediumViews = RemoteViews(context.packageName, R.layout.widget_medium).apply {
                setTextViewText(R.id.widget_medium_date, dateText)
                setTextViewText(R.id.widget_medium_balance, balanceDisplay)
                setTextViewText(R.id.widget_medium_income, incomeDisplay)
                setTextViewText(R.id.widget_medium_expenses, expensesDisplay)
                setTextViewText(R.id.widget_medium_status, financialPosition.label)
                setTextColor(R.id.widget_medium_status, statusColor)
                setOnClickPendingIntent(R.id.widget_medium_root, openAppIntent)
                setOnClickPendingIntent(R.id.widget_medium_add, quickTxIntent)
            }

            // 3. Expanded RemoteViews
            val expandedViews = RemoteViews(context.packageName, R.layout.widget_expanded).apply {
                setTextViewText(R.id.widget_expanded_date, dateText)
                setTextViewText(R.id.widget_expanded_balance, balanceDisplay)
                setTextViewText(R.id.widget_expanded_income, incomeDisplay)
                setTextViewText(R.id.widget_expanded_expenses, expensesDisplay)
                setTextViewText(R.id.widget_expanded_status, financialPosition.label)
                setTextColor(R.id.widget_expanded_status, statusColor)
                setOnClickPendingIntent(R.id.widget_expanded_root, openAppIntent)
                setOnClickPendingIntent(R.id.widget_expanded_add, quickTxIntent)

                val recentTx = transactions.take(3)
                if (recentTx.isEmpty()) {
                    setViewVisibility(R.id.widget_expanded_empty_tx, View.VISIBLE)
                    setViewVisibility(R.id.widget_expanded_tx1, View.GONE)
                    setViewVisibility(R.id.widget_expanded_tx2, View.GONE)
                    setViewVisibility(R.id.widget_expanded_tx3, View.GONE)
                } else {
                    setViewVisibility(R.id.widget_expanded_empty_tx, View.GONE)
                    val rowIds = listOf(
                        Triple(R.id.widget_expanded_tx1, R.id.widget_expanded_tx1_title, R.id.widget_expanded_tx1_amount),
                        Triple(R.id.widget_expanded_tx2, R.id.widget_expanded_tx2_title, R.id.widget_expanded_tx2_amount),
                        Triple(R.id.widget_expanded_tx3, R.id.widget_expanded_tx3_title, R.id.widget_expanded_tx3_amount)
                    )
                    rowIds.forEachIndexed { index, ids ->
                        val tx = recentTx.getOrNull(index)
                        if (tx == null) {
                            setViewVisibility(ids.first, View.GONE)
                        } else {
                            setViewVisibility(ids.first, View.VISIBLE)
                            setTextViewText(ids.second, tx.title.ifBlank { tx.category })
                            val prefix = if (tx.type == "income") "+" else "−"
                            val txColor = if (tx.type == "income") Color.parseColor("#1B9E5A") else Color.parseColor("#111111")
                            setTextViewText(
                                ids.third,
                                "$prefix${TeenageMoneyRepository.displayMoney(tx.amount, privacyMode)}"
                            )
                            setTextColor(ids.third, txColor)
                        }
                    }
                }
            }

            // Android 12+ multi-size RemoteViews
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val viewsMap = mapOf(
                    SizeF(120f, 60f) to compactViews,
                    SizeF(220f, 90f) to mediumViews,
                    SizeF(260f, 160f) to expandedViews
                )
                manager.updateAppWidget(appWidgetId, RemoteViews(viewsMap))
            } else {
                val options = manager.getAppWidgetOptions(appWidgetId)
                val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
                val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
                val selectedViews = when {
                    minHeight in 1..85 || (minWidth in 1..180 && minHeight < 140) -> compactViews
                    minHeight in 86..160 -> mediumViews
                    else -> expandedViews
                }
                manager.updateAppWidget(appWidgetId, selectedViews)
            }
        }
    }
}
