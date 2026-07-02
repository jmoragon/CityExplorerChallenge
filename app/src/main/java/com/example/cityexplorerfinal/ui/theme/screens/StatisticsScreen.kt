package com.example.cityexplorerfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cityexplorerfinal.model.Challenge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    historyList: List<Challenge>,
    onBackClick: () -> Unit
) {
    val totalCompleted = historyList.size
    val totalDistance = historyList.sumOf { it.distance }
    val categoryCounts = historyList.groupingBy { it.category }.eachCount()
    val mostExplored = categoryCounts.maxByOrNull { it.value }?.key ?: "None"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total completed challenges: $totalCompleted", fontWeight = FontWeight.Bold)
                    Text("Total explored distance: ${totalDistance / 1000} km", fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Categories visited:", fontWeight = FontWeight.SemiBold)
                    categoryCounts.forEach { (category, count) ->
                        Text("- $category: $count")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Most explored category: $mostExplored", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
