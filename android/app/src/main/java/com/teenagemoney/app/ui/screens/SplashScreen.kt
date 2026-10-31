package com.teenagemoney.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teenagemoney.app.R
import com.teenagemoney.app.ui.theme.TMBackground
import com.teenagemoney.app.ui.theme.TMMuted
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.92f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(400))
        scaleAnim.animateTo(1f, animationSpec = tween(400))
        delay(400) // Professional, crisp startup
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TMBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
                .padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_teenage_money),
                contentDescription = "Teenage Money",
                modifier = Modifier.size(width = 220.dp, height = 110.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "your money, your way.",
                fontFamily = FontFamily.SansSerif,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TMMuted,
                letterSpacing = (-0.2).sp
            )
        }
    }
}
