package com.golazo.medical.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.painterResource
import com.golazo.medical.R
import com.golazo.medical.ui.theme.UefaBlue

@Composable
fun BodyMapWithHighlights(
    selectedAreas: Set<String>,
    isFrontView: Boolean,
    modifier: Modifier = Modifier
) {
    val highlightColor = Color(0xFF90CAF9).copy(alpha = 0.4f) // Light blue highlight

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Base body image
        Image(
            painter = painterResource(
                id = if (isFrontView) R.drawable.body_front else R.drawable.body_back
            ),
            contentDescription = if (isFrontView) "Body front view" else "Body back view",
            modifier = Modifier.fillMaxSize()
        )

        // Overlay highlight dots at body part positions
        // Positions from reference: Head=12%, Neck=18%, Shoulder=24%, Chest=32%, Hip=52%, Thigh=63%, Knee=74%, LowerLeg=84%, Ankle=92%, Foot=96%
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w * 0.5f  // Center X (50%)
            val dotRadius = w * 0.06f  // Size of highlight dot

            // Helper function to draw a glowing dot
            fun drawHighlightDot(x: Float, y: Float, radius: Float = dotRadius) {
                // Outer glow
                drawCircle(
                    color = highlightColor.copy(alpha = 0.3f),
                    radius = radius * 1.5f,
                    center = Offset(x, y)
                )
                // Inner dot
                drawCircle(
                    color = highlightColor,
                    radius = radius,
                    center = Offset(x, y)
                )
            }

            // Head - 50% x, 12% y
            if (selectedAreas.contains("Head")) {
                drawHighlightDot(cx, h * 0.06f)
            }

            // Neck - below head
            if (selectedAreas.contains("Neck")) {
                drawHighlightDot(cx, h * 0.16f, dotRadius * 0.7f)
            }

            // Shoulder - on the shoulder joints
            if (selectedAreas.contains("Shoulder")) {
                drawHighlightDot(cx - w * 0.13f, h * 0.22f)  // Left
                drawHighlightDot(cx + w * 0.13f, h * 0.22f)  // Right
            }

            // Chest
            if (isFrontView && selectedAreas.contains("Chest")) {
                drawHighlightDot(cx, h * 0.28f)
            }

            // Back
            if (!isFrontView && selectedAreas.contains("Back")) {
                drawHighlightDot(cx, h * 0.28f)
            }

            // Hip/Groin - at pelvis area
            if (selectedAreas.contains("Hip/Groin")) {
                drawHighlightDot(cx, h * 0.46f)
            }

            // Thigh - on upper legs
            if (selectedAreas.contains("Thigh (Front)") || selectedAreas.contains("Thigh (Back)")) {
                drawHighlightDot(cx - w * 0.07f, h * 0.55f)  // Left
                drawHighlightDot(cx + w * 0.07f, h * 0.55f)  // Right
            }

            // Knee - on the knee joints
            if (selectedAreas.contains("Knee")) {
                drawHighlightDot(cx - w * 0.055f, h * 0.66f)  // Left
                drawHighlightDot(cx + w * 0.055f, h * 0.66f)  // Right
            }

            // Lower Leg / Calf
            if (selectedAreas.contains("Lower Leg") || selectedAreas.contains("Calf")) {
                drawHighlightDot(cx - w * 0.055f, h * 0.74f)  // Left
                drawHighlightDot(cx + w * 0.055f, h * 0.74f)  // Right
            }

            // Ankle
            if (selectedAreas.contains("Ankle")) {
                drawHighlightDot(cx - w * 0.055f, h * 0.86f)  // Left
                drawHighlightDot(cx + w * 0.055f, h * 0.86f)  // Right
            }

            // Foot
            if (selectedAreas.contains("Foot")) {
                drawHighlightDot(cx - w * 0.06f, h * 0.92f)  // Left
                drawHighlightDot(cx + w * 0.06f, h * 0.92f)  // Right
            }
        }
    }
}
