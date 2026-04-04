package com.grocery.manager.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grocery.manager.ui.theme.Charcoal900
import com.grocery.manager.ui.theme.Gold
import com.grocery.manager.ui.theme.Teal500
import com.grocery.manager.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {

    // Animation states
    val logoScale = remember { Animatable(0.6f) }
    val logoAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val dividerWidth = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1 — logo pops in
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        logoAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )

        // Step 2 — divider draws across
        dividerWidth.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )

        // Step 3 — tagline fades in
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400)
        )

        // Step 4 — hold then navigate
        delay(800)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Charcoal900),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // GM Monogram
            Box(
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer teal ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            color = Teal500.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.extraLarge
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner teal box
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                color = Teal500.copy(alpha = 0.25f),
                                shape = MaterialTheme.shapes.large
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GM",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Teal500,
                            letterSpacing = (-1).sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Wordmark
            Text(
                text = "GROCERY",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 6.sp,
                modifier = Modifier.alpha(logoAlpha.value)
            )
            Text(
                text = "MANAGER",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Teal500,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(logoAlpha.value)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated divider
            Box(
                modifier = Modifier
                    .fillMaxWidth(dividerWidth.value * 0.4f)
                    .height(2.dp)
                    .background(Gold.copy(alpha = 0.7f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tagline
            Text(
                text = "Your prices. Your profit. Always.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(taglineAlpha.value)
            )
        }

        // Version tag at bottom
        Text(
            text = "v1.0",
            fontSize = 11.sp,
            color = TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(taglineAlpha.value)
        )
    }
}