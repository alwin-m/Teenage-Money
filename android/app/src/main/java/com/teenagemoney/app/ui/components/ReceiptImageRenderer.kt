package com.teenagemoney.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.FileProvider
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import java.io.File

fun shareableReceiptUri(
    context: Context,
    transaction: TransactionItem,
    userName: String,
    amountsMasked: Boolean,
    balanceMasked: Boolean
) = renderReceiptImage(context, transaction, userName, amountsMasked, balanceMasked).let { file ->
    FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun renderReceiptImage(
    context: Context,
    transaction: TransactionItem,
    userName: String,
    amountsMasked: Boolean,
    balanceMasked: Boolean
): File {
    val receiptId = transaction.receiptNumber.ifEmpty { "TM-${transaction.id}" }
    val bitmap = Bitmap.createBitmap(1080, 1540, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(Color.WHITE)

    val ink = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(35, 34, 43) }
    val muted = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(111, 107, 114) }
    val coral = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(225, 96, 76) }
    val mono = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    val monoBold = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    val sansBold = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)

    fun text(value: String, x: Float, y: Float, size: Float, paint: Paint, typeface: Typeface = Typeface.SANS_SERIF, align: Paint.Align = Paint.Align.LEFT) {
        paint.textSize = size
        paint.typeface = typeface
        paint.textAlign = align
        canvas.drawText(value, x, y, paint)
    }

    text("TEENAGE MONEY", 540f, 110f, 32f, ink, sansBold, Paint.Align.CENTER)
    text("Thank you, ${userName.ifBlank { "there" }}", 540f, 178f, 42f, ink, sansBold, Paint.Align.CENTER)
    text("Your expense has been saved successfully", 540f, 222f, 22f, muted, mono, Paint.Align.CENTER)

    canvas.drawRect(70f, 270f, 1010f, 272f, muted)
    text("RECEIPT ID", 90f, 330f, 18f, muted, monoBold)
    text(receiptId, 90f, 365f, 24f, ink, monoBold)
    text("AMOUNT", 990f, 330f, 18f, muted, monoBold, Paint.Align.RIGHT)
    text(TeenageMoneyRepository.displayMoney(transaction.amount, amountsMasked), 990f, 365f, 30f, ink, monoBold, Paint.Align.RIGHT)

    text("EXPENSE", 90f, 450f, 18f, muted, monoBold)
    text(transaction.title.ifBlank { "Expense" }, 90f, 488f, 28f, ink, sansBold)
    text(transaction.category, 90f, 526f, 22f, muted, mono)
    text("DATE & TIME", 90f, 604f, 18f, muted, monoBold)
    text(transaction.timestamp, 90f, 640f, 24f, ink, mono)

    canvas.drawRect(70f, 700f, 1010f, 702f, muted)
    text("SPENDING BALANCE", 90f, 760f, 18f, muted, monoBold)
    text(
        if (balanceMasked) "₹ ••••" else TeenageMoneyRepository.formatMoney(transaction.remaining),
        90f,
        805f,
        32f,
        coral,
        monoBold
    )

    val modules = code128BModules(receiptId)
    val left = 90f
    val width = 900f
    val moduleWidth = width / modules.size
    modules.forEachIndexed { index, isBar ->
        if (isBar) canvas.drawRect(left + index * moduleWidth, 900f, left + (index + 1) * moduleWidth, 1030f, ink)
    }
    text(receiptId, 540f, 1075f, 20f, ink, mono, Paint.Align.CENTER)

    text("Shared from Teenage Money", 540f, 1330f, 20f, muted, mono, Paint.Align.CENTER)
    text("Your money, your way.", 540f, 1370f, 22f, coral, sansBold, Paint.Align.CENTER)

    val directory = File(context.cacheDir, "shared").apply { mkdirs() }
    val file = File(directory, "receipt-${transaction.id}.png")
    file.outputStream().use { output -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, output) }
    bitmap.recycle()
    return file
}
