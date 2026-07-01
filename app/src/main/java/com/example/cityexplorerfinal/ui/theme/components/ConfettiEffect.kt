package com.example.cityexplorerfinal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun ConfettiEffect(onAnimationEnd: () -> Unit) {
    val animateY = remember { Animatable(-100f) }

    LaunchedEffect(Unit) {
        animateY.animateTo(
            targetValue = 2000f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
        )
        delay(500)
        onAnimationEnd()
    }

    val particles = remember {
        List(100) {
            ConfettiParticle(
                x = Random.nextFloat() * 1000f,
                speed = Random.nextFloat() * 2f + 1f,
                color = listOf(Color(0xFFB5EAD7), Color(0xFFA2C2E1), Color(0xFFFFD3B6), Color(0xFFFFFFD1)).random()
            )
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawCircle(
                color = particle.color,
                radius = 15f,
                center = Offset(particle.x, animateY.value * particle.speed)
            )
        }
    }
}

data class ConfettiParticle(val x: Float, val speed: Float, val color: Color)