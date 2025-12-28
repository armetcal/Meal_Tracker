package com.example.mealtracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.data.entities.DailyGoal
import com.example.mealtracker.data.entities.MealLog
import com.example.mealtracker.data.repository.RecipeRepository
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RecipeViewModel(
    private val context: Context,
    private val repository: RecipeRepository
) : ViewModel() {

    // State for recipes
    val allRecipes: Flow<List<Recipe>> = repository.getAllRecipes()

    // State for daily goals
    val allGoals: Flow<List<DailyGoal>> = repository.getAllGoals()

    // State for today's meal logs - use String date
    val todayMealLogs: Flow<List<MealLog>> = repository.getMealLogsForDate(getCurrentDate())

    // State for error messages
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        // Initialize default goals if app is starting fresh
        initializeDefaultGoals()
    }

    // Recipe operations (keep existing)
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

    // Daily Goal operations (keep existing)
    fun insertGoal(goal: DailyGoal) {
        viewModelScope.launch {
            try {
                repository.insertGoal(goal)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save goal: ${e.message}"
            }
        }
    }

    fun updateGoal(goal: DailyGoal) {
        viewModelScope.launch {
            try {
                repository.updateGoal(goal)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update goal: ${e.message}"
            }
        }
    }

    // Meal Log operations (keep existing)
    fun insertMealLog(mealLog: MealLog) {
        viewModelScope.launch {
            try {
                repository.insertMealLog(mealLog)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to log meal: ${e.message}"
            }
        }
    }

    fun deleteMealLog(mealLog: MealLog) {
        viewModelScope.launch {
            try {
                repository.deleteMealLog(mealLog)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete meal log: ${e.message}"
            }
        }
    }

    // Get goal for specific day
    fun getGoalForDay(dayOfWeek: String): Flow<DailyGoal?> {
        return repository.getGoalForDay(dayOfWeek)
    }

    // Get meal logs for specific date - now uses String (FIXED)
    fun getMealLogsForDate(date: String): Flow<List<MealLog>> {
        return repository.getMealLogsForDate(date)
    }

    // Initialize default goals when app starts
    private fun initializeDefaultGoals() {
        viewModelScope.launch {
            try {
                val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

                // Get the current goals once
                val currentGoals = repository.getAllGoals()

                // Use first() to get the current list once
                val goals = currentGoals.first()

                if (goals.isEmpty()) {
                    // Create default goals (150g protein, 200g carbs, 50g fat)
                    daysOfWeek.forEach { day ->
                        repository.insertGoal(DailyGoal(
                            dayOfWeek = day,
                            proteinGoal = 150.0,
                            carbsGoal = 200.0,
                            fatGoal = 50.0
                        ))
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to initialize goals: ${e.message}"
            }
        }
    }

    // Helper function to get current date as string in "yyyy-MM-dd" format
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Remove or comment out this method for now - we'll implement it properly later
    /*
    fun getRecipeById(id: Long): Flow<Recipe?> {
        // This would need proper implementation in your repository
        // For now, filter from the existing flow
        return allRecipes.map { recipes -> recipes.find { it.id == id } }
    }
    */

    // Add this method to your RecipeViewModel class
    fun getTodaysTotals(): Map<String, Double> {
        // This will be calculated from the meal logs
        // For now, we'll return a placeholder - we'll implement the actual calculation next
        return mapOf(
            "protein" to 0.0,
            "carbs" to 0.0,
            "fat" to 0.0,
            "calories" to 0.0
        )
    }


}
