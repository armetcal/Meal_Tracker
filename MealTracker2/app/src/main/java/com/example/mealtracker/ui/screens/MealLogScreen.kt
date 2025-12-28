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
import com.example.mealtracker.data.entities.MealLog
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.ui.viewmodels.RecipeViewModel
import com.example.mealtracker.ui.viewmodels.RecipeViewModelFactory
import android.R
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealLogScreen(
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit
) {
    val recipes by viewModel.allRecipes.collectAsState(emptyList())
    val mealLogs by viewModel.todayMealLogs.collectAsState(emptyList())
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Debug logging
    LaunchedEffect(mealLogs) {
        println("Current meal logs count: ${mealLogs.size}")
        mealLogs.forEach { log ->
            println("Meal Log: recipeId=${log.recipeId}, servings=${log.servingsConsumed}, date=${log.date}")
        }
    }

    var selectedRecipeId by remember { mutableStateOf<Long?>(null) }
    var servings by remember { mutableStateOf("1") }

    // Get current date using Calendar for display
    val currentDate = getCurrentDate()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Meal Log - $currentDate")
                }
                // No back button for home screen
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Log new meal section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Log New Meal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Recipe dropdown
                    if (recipes.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        val selectedRecipe = recipes.find { it.id == selectedRecipeId }

                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { expanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(selectedRecipe?.name ?: "Select Recipe")
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                recipes.forEach { recipe ->
                                    DropdownMenuItem(
                                        text = { Text(recipe.name) },
                                        onClick = {
                                            selectedRecipeId = recipe.id ?: 0L
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            "No recipes available. Add recipes first.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Servings input
                    OutlinedTextField(
                        value = servings,
                        onValueChange = { servings = it },
                        label = { Text("Servings") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        singleLine = true
                    )

                    // Log button
                    Button(
                        onClick = {
                            selectedRecipeId?.let { recipeId ->
                                // Use the database date format (yyyy-MM-dd) for storage
                                val databaseDate = getDatabaseDate()
                                val newMealLog = MealLog(
                                    recipeId = recipeId,
                                    servingsConsumed = servings.toDoubleOrNull() ?: 1.0,
                                    date = databaseDate
                                )
                                viewModel.insertMealLog(newMealLog)
                                servings = "1"
                                selectedRecipeId = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        enabled = selectedRecipeId != null && recipes.isNotEmpty()
                    ) {
                        Text("Log Meal")
                    }
                }
            }

            // Today's logged meals
            Text(
                "Today's Meals (${mealLogs.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (mealLogs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No meals logged today",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mealLogs) { mealLog ->
                        MealLogCard(
                            mealLog = mealLog,
                            recipes = recipes,
                            onDelete = {
                                viewModel.deleteMealLog(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

// Helper function for database storage format (yyyy-MM-dd)
private fun getDatabaseDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return dateFormat.format(calendar.time)
}

// Helper function to format date for display (MMM d)
private fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault())
    return dateFormat.format(calendar.time)
}

@Composable
fun MealLogCard(
    mealLog: MealLog,
    recipes: List<Recipe>,
    onDelete: (MealLog) -> Unit
) {
    val recipe = recipes.find { it.id == mealLog.recipeId }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = recipe?.name ?: "Unknown Recipe",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${mealLog.servingsConsumed} servings",
                    style = MaterialTheme.typography.bodySmall
                )
                recipe?.let {
                    Text(
                        text = "Protein: ${(it.proteinPerServing * mealLog.servingsConsumed).toInt()}g • " +
                                "Carbs: ${(it.carbsPerServing * mealLog.servingsConsumed).toInt()}g • " +
                                "Fat: ${(it.fatPerServing * mealLog.servingsConsumed).toInt()}g",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            IconButton(onClick = { onDelete(mealLog) }) {
                Icon(
                    painterResource(R.drawable.ic_menu_delete),
                    contentDescription = "Delete"
                )
            }
        }
    }
}
