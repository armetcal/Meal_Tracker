package com.example.mealtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mealtracker.ui.screens.AddRecipeScreen
import com.example.mealtracker.ui.screens.RecipeListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("recipeList") }

                    when (currentScreen) {
                        "recipeList" -> {
                            RecipeListScreen(
                                onAddRecipe = {
                                    currentScreen = "addRecipe"
                                },
                                onRecipeClick = { recipeId ->
                                    // Will implement recipe details later
                                }
                            )
                        }
                        "addRecipe" -> {
                            AddRecipeScreen(
                                onBack = {
                                    currentScreen = "recipeList"
                                },
                                onRecipeAdded = {
                                    currentScreen = "recipeList"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
