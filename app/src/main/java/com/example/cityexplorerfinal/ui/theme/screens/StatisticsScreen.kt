package com.example.cityexplorerfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cityexplorerfinal.model.Challenge
import com.example.cityexplorerfinal.ui.theme.PastelPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    historyList: List<Challenge>,
    onBackClick: () -> Unit
) {
    val totalCompleted = historyList.size
    val totalDistance = historyList.sumOf { it.distance }
    val favoriteCategory = historyList.groupingBy { it.category }.eachCount().maxByOrNull { it.value }?.key ?: "None"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Your Statistics") },
                navigationIcon = {
                    Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                        Text(text = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = PastelPink.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    StatRow(label = "Total Completed", value = "$totalCompleted")
                    Spacer(modifier = Modifier.height(16.dp))
                    StatRow(label = "Total Distance", value = "${totalDistance / 1000} km")
                    Spacer(modifier = Modifier.height(16.dp))
                    StatRow(label = "Favorite Category", value = favoriteCategory)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
    }
}

