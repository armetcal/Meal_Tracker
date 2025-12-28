package com.example.mealtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MacroProgressBar(
    label: String,
    current: Double,
    goal: Double,
    unit: String = "g",
    modifier: Modifier = Modifier
) {
    val progress = if (goal > 0) (current / goal).coerceIn(0.0, 1.0) else 0.0
    val percentage = (progress * 100).toInt()

    // Color coding based on progress
    val color = when {
        progress < 0.7 -> Color(0xFF4CAF50) // Green for under 70%
        progress < 1.0 -> Color(0xFFFF9800) // Orange for 70-100%
        else -> Color(0xFFF44336) // Red for over 100%
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Label and values
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = "${current.toInt()}${unit} / ${goal.toInt()}${unit} ($percentage%)",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )
        }

        // Progress bar
        LinearProgressIndicator(
            progress = progress.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
