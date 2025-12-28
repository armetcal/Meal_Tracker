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
import androidx.compose.ui.Alignment  // ADD THIS IMPORT

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    recipeId: Long,
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit,
    onRecipeUpdated: () -> Unit
) {
    val recipes by viewModel.allRecipes.collectAsState(emptyList())

    // Debug logging
    LaunchedEffect(recipeId, recipes) {
        println("EditRecipeScreen: recipeId=$recipeId, totalRecipes=${recipes.size}")
        recipes.forEach { r ->
            println("Recipe: id=${r.id}, name=${r.name}")
        }
        val foundRecipe = recipes.find { it.id == recipeId }
        println("Found recipe: $foundRecipe")
    }

    val recipe = remember(recipeId, recipes) {
        recipes.find { it.id == recipeId }
    }

    // Use mutable state for editing
    var recipeName by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var servingSize by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Update the fields when the recipe loads
    LaunchedEffect(recipe) {
        if (recipe != null) {
            recipeName = recipe.name
            protein = recipe.proteinPerServing.toString()
            carbs = recipe.carbsPerServing.toString()
            fat = recipe.fatPerServing.toString()
            servingSize = recipe.servingSize
            println("Fields updated for recipe: $recipeName")
        } else {
            println("Recipe not found, keeping default values")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Recipe ${if (recipe == null) "(Loading...)" else ""}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(android.R.drawable.ic_menu_revert),
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

            // Show loading state with more info
            if (recipe == null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Loading recipe...",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Recipe ID: $recipeId",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Total recipes in database: ${recipes.size}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else {
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

                // Update button
                Button(
                    onClick = {
                        if (recipeName.isBlank()) {
                            errorMessage = "Please enter a recipe name"
                            return@Button
                        }

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
