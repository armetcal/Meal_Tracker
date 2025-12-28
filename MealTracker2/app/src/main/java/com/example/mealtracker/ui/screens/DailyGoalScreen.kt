package com.example.mealtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.data.entities.DailyGoal
import com.example.mealtracker.ui.viewmodels.RecipeViewModel
import com.example.mealtracker.ui.viewmodels.RecipeViewModelFactory
import android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyGoalScreen(
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit
) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val allGoals by viewModel.allGoals.collectAsState(emptyList())
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Daily Goals") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_menu_revert),
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Error message
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysOfWeek) { day ->
                    // Use key to ensure recomposition when goals change
                    key(day) {
                        DailyGoalCard(
                            day = day,
                            goal = allGoals.find { it.dayOfWeek == day },
                            onSaveGoal = { protein, carbs, fat ->
                                val newGoal = DailyGoal(
                                    dayOfWeek = day,
                                    proteinGoal = protein,
                                    carbsGoal = carbs,
                                    fatGoal = fat
                                )
                                // Update if exists, insert if new
                                val existingGoal = allGoals.find { it.dayOfWeek == day }
                                if (existingGoal != null) {
                                    viewModel.updateGoal(newGoal)
                                } else {
                                    viewModel.insertGoal(newGoal)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyGoalCard(
    day: String,
    goal: DailyGoal?,
    onSaveGoal: (Double, Double, Double) -> Unit
) {
    // Use derivedStateOf to ensure the text fields update when the goal changes
    val proteinState = remember(goal) { derivedStateOf { goal?.proteinGoal?.toString() ?: "" } }
    val carbsState = remember(goal) { derivedStateOf { goal?.carbsGoal?.toString() ?: "" } }
    val fatState = remember(goal) { derivedStateOf { goal?.fatGoal?.toString() ?: "" } }

    // Local mutable state for editing
    var protein by remember { mutableStateOf(proteinState.value) }
    var carbs by remember { mutableStateOf(carbsState.value) }
    var fat by remember { mutableStateOf(fatState.value) }

    // Update local state when the goal changes
    LaunchedEffect(goal) {
        protein = goal?.proteinGoal?.toString() ?: ""
        carbs = goal?.carbsGoal?.toString() ?: ""
        fat = goal?.fatGoal?.toString() ?: ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = day,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Protein
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein (g)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Carbs
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text("Carbs (g)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Fat
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat (g)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Save button
            Button(
                onClick = {
                    val proteinValue = protein.toDoubleOrNull() ?: 0.0
                    val carbsValue = carbs.toDoubleOrNull() ?: 0.0
                    val fatValue = fat.toDoubleOrNull() ?: 0.0
                    onSaveGoal(proteinValue, carbsValue, fatValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Save Goals")
            }

            // Display calculated calories
            if (protein.isNotBlank() || carbs.isNotBlank() || fat.isNotBlank()) {
                val proteinValue = protein.toDoubleOrNull() ?: 0.0
                val carbsValue = carbs.toDoubleOrNull() ?: 0.0
                val fatValue = fat.toDoubleOrNull() ?: 0.0
                val calories = (proteinValue * 4) + (carbsValue * 4) + (fatValue * 9)

                Text(
                    text = "Calories: ${calories.toInt()}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
