package com.teenagemoney.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun OrbitVisual(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val centerX = width * 0.82f
        val centerY = height * 0.58f

        // Orbit ellipse 1
        drawOval(
            color = Color.White.copy(alpha = 0.08f),
            topLeft = Offset(width * 0.25f, height * 0.15f),
            size = Size(width * 0.85f, height * 0.85f),
            style = Stroke(width = 1.5f)
        )

        // Orbit ellipse 2 (tilted wide)
        drawOval(
            color = Color.White.copy(alpha = 0.06f),
            topLeft = Offset(width * 0.10f, height * 0.28f),
            size = Size(width * 0.95f, height * 0.65f),
            style = Stroke(width = 1.2f)
        )

        // Orbit ellipse 3
        drawOval(
            color = Color.White.copy(alpha = 0.04f),
            topLeft = Offset(width * 0.35f, height * 0.35f),
            size = Size(width * 0.70f, height * 0.55f),
            style = Stroke(width = 1.0f)
        )

        // Glowing Sun / Planet
        val sunCenter = Offset(centerX, centerY)
        val sunRadius = 26f

        // Glow ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF9C852).copy(alpha = 0.28f),
                    Color(0xFFE95E3F).copy(alpha = 0.08f),
                    Color.Transparent
                ),
                center = sunCenter,
                radius = sunRadius * 2.2f
            ),
            center = sunCenter,
            radius = sunRadius * 2.2f
        )

        // Solid sun disc
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFDF7A), Color(0xFFEBA63C)),
                center = Offset(sunCenter.x - 4f, sunCenter.y - 4f),
                radius = sunRadius
            ),
            center = sunCenter,
            radius = sunRadius
        )

        // Small orbiting satellites/dots
        drawCircle(
            color = Color(0xFFF4A88A),
            center = Offset(width * 0.66f, height * 0.32f),
            radius = 4.5f
        )

        drawCircle(
            color = Color(0xFFE7ECF7),
            center = Offset(width * 0.48f, height * 0.70f),
            radius = 3.5f
        )

        drawCircle(
            color = Color(0xFFBFD2EC),
            center = Offset(width * 0.84f, height * 0.82f),
            radius = 4.0f
        )
    }
}
