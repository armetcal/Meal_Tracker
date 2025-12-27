package com.example.mealtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
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
                    RecipeListScreen(
                        onAddRecipe = {
                            // TODO: Navigate to add recipe screen
                        },
                        onRecipeClick = { recipeId ->
                            // TODO: Navigate to recipe detail screen
                        }
                    )
                }
            }
        }
    }
}
