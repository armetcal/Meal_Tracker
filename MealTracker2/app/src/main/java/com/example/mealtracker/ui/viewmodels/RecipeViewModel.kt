package com.example.mealtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.data.entities.DailyGoal
import com.example.mealtracker.data.entities.MealLog
import com.example.mealtracker.data.repository.RecipeRepository
import com.example.mealtracker.data.database.MacroDatabase
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecipeViewModel(context: Context) : ViewModel() {
    // Create repository inside ViewModel
    private val repository: RecipeRepository

    // State for recipes
    val allRecipes: Flow<List<Recipe>>

    // State for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val database = MacroDatabase.getDatabase(context)
        repository = RecipeRepository(
            recipeDao = database.recipeDao(),
            mealLogDao = database.mealLogDao(),
            dailyGoalDao = database.dailyGoalDao()
        )

        // Set up the flow
        allRecipes = repository.getAllRecipes()

        // Load initial data
        loadTodaysData()
    }

    // Recipe operations
    fun insertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.insertRecipe(recipe)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add recipe: ${e.message}"
            }
        }
    }

    fun updateRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipe)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update recipe: ${e.message}"
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipe)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete recipe: ${e.message}"
            }
        }
    }

    // Data loading
    private fun loadTodaysData() {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.message}"
            }
        }
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}
