package com.teenagemoney.app.ui.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.data.TeenageMoneyRepository
import com.teenagemoney.app.data.TransactionItem
import com.teenagemoney.app.ui.theme.TMCoral
import com.teenagemoney.app.ui.theme.TMInk
import com.teenagemoney.app.ui.theme.TMLine
import com.teenagemoney.app.ui.theme.TMMuted
import com.teenagemoney.app.ui.theme.TMWhite

@Composable
fun ReceiptDialog(
    transaction: TransactionItem,
    amountsMasked: Boolean = false,
    userName: String = "",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val receiptId = transaction.receiptNumber.ifEmpty { "TM-${transaction.id}" }
    val amountText = TeenageMoneyRepository.displayMoney(transaction.amount, amountsMasked)
    val remainingText = TeenageMoneyRepository.displayMoney(transaction.remaining, amountsMasked)
    var shareBalanceMasked by remember { mutableStateOf(amountsMasked) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .shadow(20.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(TMWhite)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Intercept click inside
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✦", fontSize = 46.sp, color = TMCoral)
                    Text("Thank you, ${userName.ifBlank { "there" }}!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TMInk)
                    Text("Your expense has been saved successfully", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TMMuted, textAlign = TextAlign.Center)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().height(22.dp).background(TMWhite),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(14.dp).height(22.dp).clip(RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp)).background(Color(0xFF6F6B72)))
                    DashedDivider(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.width(14.dp).height(22.dp).clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)).background(Color(0xFF6F6B72)))
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ReceiptField("RECEIPT ID", receiptId, Modifier.weight(1f))
                        ReceiptField("AMOUNT", amountText, Modifier.weight(1f), alignEnd = true)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ReceiptField("DATE & TIME", transaction.timestamp, Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFFF5F3F1)).padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.width(30.dp).height(30.dp), contentAlignment = Alignment.Center) {
                            Box(modifier = Modifier.width(22.dp).height(22.dp).clip(RoundedCornerShape(12.dp)).background(TMCoral))
                            Box(modifier = Modifier.width(22.dp).height(22.dp).padding(start = 9.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF5644D0)))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Spending room", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TMInk)
                            Text("Balance left $remainingText", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TMMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    DashedDivider()
                    Spacer(modifier = Modifier.height(8.dp))
                    BarcodePattern(receiptNumber = receiptId)
                    Text(receiptId.chunked(4).joinToString(" "), fontFamily = FontFamily.Monospace, fontSize = 10.sp, letterSpacing = 1.5.sp, color = TMInk)
                }

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 0.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = TMCoral, contentColor = TMWhite),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text("Done", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                    TextButton(
                        onClick = {
                            shareBalanceMasked = !shareBalanceMasked
                        }
                    ) {
                        Icon(
                            imageVector = if (shareBalanceMasked) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = TMMuted
                        )
                        Text(
                            if (shareBalanceMasked) "  Balance hidden" else "  Balance visible",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TMMuted
                        )
                    }
                    TextButton(
                        onClick = {
                            try {
                                val uri = shareableReceiptUri(
                                    context = context,
                                    transaction = transaction,
                                    userName = userName,
                                    amountsMasked = amountsMasked,
                                    balanceMasked = shareBalanceMasked
                                )
                                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }, "Share receipt image"))
                            } catch (error: Exception) {
                                Toast.makeText(context, error.message ?: "Could not share receipt", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) { Text("↗  Share receipt image", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TMMuted) }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ReceiptField(
    label: String,
    value: String,
    modifier: Modifier,
    alignEnd: Boolean = false
) {
    Column(modifier = modifier, horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
        Text(label, fontFamily = FontFamily.Monospace, fontSize = 10.sp, letterSpacing = 1.sp, color = TMMuted)
        Text(value, fontFamily = FontFamily.Monospace, fontSize = if (label == "AMOUNT") 20.sp else 11.sp, fontWeight = FontWeight.Bold, color = TMInk, textAlign = if (alignEnd) TextAlign.End else TextAlign.Start)
    }
}

/*
 * Kept below for the legacy receipt body while the ticket presentation above
 * remains the active design. It is intentionally unreachable until the old
 * receipt layout is removed in a later cleanup.
 */
@Composable
private fun LegacyReceiptBody(
    transaction: TransactionItem,
    onDismiss: () -> Unit
) {
    val warning = "Spending remaining: ${TeenageMoneyRepository.formatMoney(transaction.remaining)}"
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
                Text(
                    text = "TEENAGE MONEY",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 2.sp,
                    color = TMInk
                )
                Text(
                    text = "MONEY RECEIPT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TMMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "NO. ${transaction.receiptNumber.ifEmpty { "TM-26-08-06-0001" }}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                Text(
                    text = transaction.timestamp,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TMMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DashedDivider()

                // Items header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "ITEM", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TMMuted)
                    Text(text = "PRICE", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TMMuted)
                }

                // Item row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.title,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TMInk
                    )
                    Text(
                        text = TeenageMoneyRepository.formatMoney(transaction.amount),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TMInk
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = transaction.category,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TMMuted
                    )
                    Text(
                        text = transaction.source,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TMMuted
                    )
                }

                DashedDivider()

                // Total
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TOTAL",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TMInk
                    )
                    Text(
                        text = TeenageMoneyRepository.formatMoney(transaction.amount),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TMInk
                    )
                }

                // Paid from & Remaining
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "PAID FROM", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TMMuted)
                    Text(text = "Spending / ${transaction.source}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TMInk)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "REMAINING", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TMMuted)
                    Text(
                        text = TeenageMoneyRepository.formatMoney(transaction.remaining),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TMCoral
                    )
                }

                DashedDivider()

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = warning,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TMInk,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Thermal barcode simulation
                Spacer(modifier = Modifier.height(14.dp))
                BarcodePattern(receiptNumber = transaction.receiptNumber)

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "THANK YOU.\nYour future self has received the receipt.",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    color = TMMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Tap anywhere to return",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TMCoral,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(6.dp)
                )
            }
        }

@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    Text(
        text = "- - - - - - - - - - - - - - - - - - - - - -",
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = TMLine,
        maxLines = 1,
        modifier = modifier.padding(vertical = 6.dp)
    )
}

@Composable
fun BarcodePattern(receiptNumber: String) {
    val modules = remember(receiptNumber) { code128BModules(receiptNumber) }
    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(34.dp),
    ) {
        val quietZoneModules = 10f
        val moduleWidth = size.width / (modules.size + quietZoneModules * 2)
        modules.forEachIndexed { index, isBar ->
            if (isBar) {
                drawRect(
                    color = Color(0xFF23222B),
                    topLeft = Offset((quietZoneModules + index) * moduleWidth, 0f),
                    size = Size(moduleWidth, size.height)
                )
            }
        }
    }
}
