package com.example.mealtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.ui.viewmodels.RecipeViewModel
import com.example.mealtracker.ui.viewmodels.RecipeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit,
    onRecipeAdded: () -> Unit
) {
    var recipeName by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_revert),
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Recipe name
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Recipe Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Protein
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Carbs
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text("Carbs (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Fat
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Serving size
            OutlinedTextField(
                value = servingSize,
                onValueChange = { servingSize = it },
                label = { Text("Serving Size (e.g., '1 cup', '100g')") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Add button
            Button(
                onClick = {
                    if (recipeName.isBlank()) {
                        errorMessage = "Please enter a recipe name"
                        return@Button
                    }

                    // Use default values if fields are empty
                    val newRecipe = Recipe(
                        name = recipeName,
                        proteinPerServing = protein.toDoubleOrNull() ?: 0.0,
                        carbsPerServing = carbs.toDoubleOrNull() ?: 0.0,
                        fatPerServing = fat.toDoubleOrNull() ?: 0.0,
                        servingSize = if (servingSize.isBlank()) "1 serving" else servingSize
                    )

                    viewModel.insertRecipe(newRecipe)
                    onRecipeAdded()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Recipe")
            }

            // Quick-add recipe for testing
            Button(
                onClick = {
                    val newRecipe = Recipe(
                        name = "Quick Test: $recipeName",
                        proteinPerServing = 25.0,
                        carbsPerServing = 15.0,
                        fatPerServing = 10.0,
                        servingSize = "1 serving"
                    )
                    viewModel.insertRecipe(newRecipe)
                    onRecipeAdded()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text("Add Quick Test Recipe")
            }
        }
    }
}
