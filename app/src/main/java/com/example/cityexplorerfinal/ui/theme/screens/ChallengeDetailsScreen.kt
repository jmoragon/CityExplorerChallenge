package com.example.cityexplorerfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cityexplorerfinal.model.Challenge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeDetailsScreen(
    challenge: Challenge,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Challenge Details") },
                navigationIcon = { Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) { Text("Back") } }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {

            // TITULO DEL RETO
            Text("Challenge: ${challenge.title.replace("Explore: ", "Visit ")}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // RAZONAMIENTO DEL MOTOR LÓGICO
            Text("Reason:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = challenge.reasoning ?: "- Generated based on your location and history.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // DETALLES DEL LUGAR
            Text("Place details:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${challenge.title.replace("Explore: ", "")}")
                    Text("Category: ${challenge.category}")
                    Text("Distance: ${challenge.distance} m")
                }
            }

            // IMAGEN DE LA API DE GOOGLE PLACES
            if (challenge.photoUrl != null) {
                Spacer(modifier = Modifier.height(24.dp))
                AsyncImage(
                    model = challenge.photoUrl,
                    contentDescription = "Photo of ${challenge.title}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray)
                )
            }
        }
    }
}
