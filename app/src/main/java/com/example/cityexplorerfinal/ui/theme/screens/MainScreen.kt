package com.example.cityexplorerfinal.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cityexplorerfinal.model.Challenge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainScreen(
    activeChallenge: Challenge?,
    historyList: List<Challenge>, // Necesario para calcular el progreso
    onOpenMapClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onGenerateNewClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit
) {
    // Calculando progreso (Requisito 4.2.1)
    val totalCompleted = historyList.size
    val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val completedToday = historyList.count { it.completionTime?.startsWith(todayStr) == true }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("City Explorer Challenge", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            if (activeChallenge != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Active Challenge", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(activeChallenge.title)
                        Text(activeChallenge.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Category: ${activeChallenge.category}")
                        Text("Distance: ${activeChallenge.distance} m")
                        Text("Status: ${activeChallenge.status}")
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = onOpenMapClick) { Text("Open Map") }
                            Button(onClick = onDetailsClick) { Text("Details") }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = onGenerateNewClick, modifier = Modifier.fillMaxWidth()) {
                            Text("New Challenge")
                        }
                    }
                }
            } else {
                Button(onClick = onGenerateNewClick, modifier = Modifier.fillMaxWidth().height(60.dp)) {
                    Text("Generate Challenge")
                }
            }
        }

        // Progreso y Navegación inferior (Requisito 4.2.1)
        Column {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Progress", fontWeight = FontWeight.Bold)
                    Text("Completed today: $completedToday")
                    Text("Total completed: $totalCompleted")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = onHistoryClick) { Text("History") }
                TextButton(onClick = onStatsClick) { Text("Statistics") }
            }
        }
    }
}