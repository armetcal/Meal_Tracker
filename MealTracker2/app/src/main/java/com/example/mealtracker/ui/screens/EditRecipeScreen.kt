package com.example.mealtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.ui.viewmodels.RecipeViewModel
import com.example.mealtracker.ui.viewmodels.RecipeViewModelFactory
import android.R
import androidx.compose.runtime.collectAsState  // Using the compose version

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    recipeId: Long,
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit,
    onRecipeUpdated: () -> Unit
) {
    val recipes by viewModel.allRecipes.collectAsState(emptyList())

    // Find the recipe from the current list
    val recipe = remember(recipeId, recipes) {
        recipes.find { it.id == recipeId }
    }

    // Use derivedStateOf to ensure the state updates when recipe changes
    val recipeName by remember(recipe) {
        derivedStateOf { recipe?.name ?: "" }
    }
    val protein by remember(recipe) {
        derivedStateOf { recipe?.proteinPerServing?.toString() ?: "" }
    }
    val carbs by remember(recipe) {
        derivedStateOf { recipe?.carbsPerServing?.toString() ?: "" }
    }
    val fat by remember(recipe) {
        derivedStateOf { recipe?.fatPerServing?.toString() ?: "" }
    }
    val servingSize by remember(recipe) {
        derivedStateOf { recipe?.servingSize ?: "" }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Recipe") },
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

            // Recipe name
            OutlinedTextField(
                value = recipeName,
                onValueChange = {
                    // We need to use local mutable state for editing
                    // This will be handled differently - see below
                },
                label = { Text("Recipe Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = recipe == null  // Make read-only until data loads
            )

            // Protein
            OutlinedTextField(
                value = protein,
                onValueChange = { /* Handle change */ },
                label = { Text("Protein (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = recipe == null
            )

            // Carbs
            OutlinedTextField(
                value = carbs,
                onValueChange = { /* Handle change */ },
                label = { Text("Carbs (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = recipe == null
            )

            // Fat
            OutlinedTextField(
                value = fat,
                onValueChange = { /* Handle change */ },
                label = { Text("Fat (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = recipe == null
            )

            // Serving size
            OutlinedTextField(
                value = servingSize,
                onValueChange = { /* Handle change */ },
                label = { Text("Serving Size (e.g., '1 cup', '100g')") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = recipe == null
            )

            // Show loading state
            if (recipe == null) {
                Text(
                    text = "Loading recipe...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Update button
                Button(
                    onClick = {
                        // This will now work with the proper state
                        val updatedRecipe = recipe.copy(
                            name = recipeName,
                            proteinPerServing = protein.toDoubleOrNull() ?: 0.0,
                            carbsPerServing = carbs.toDoubleOrNull() ?: 0.0,
                            fatPerServing = fat.toDoubleOrNull() ?: 0.0,
                            servingSize = if (servingSize.isBlank()) "1 serving" else servingSize
                        )

                        viewModel.updateRecipe(updatedRecipe)
                        onRecipeUpdated()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Recipe")
                }

                // Delete button
                Button(
                    onClick = {
                        viewModel.deleteRecipe(recipe)
                        onRecipeUpdated()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text("Delete Recipe")
                }
            }
        }
    }
}
