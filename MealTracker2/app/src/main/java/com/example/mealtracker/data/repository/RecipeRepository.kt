package com.example.mealtracker.data.repository

import com.example.mealtracker.data.daos.RecipeDao
import com.example.mealtracker.data.daos.MealLogDao
import com.example.mealtracker.data.daos.DailyGoalDao
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.data.entities.DailyGoal
import com.example.mealtracker.data.entities.MealLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val mealLogDao: MealLogDao,
    private val dailyGoalDao: DailyGoalDao
) {

    // Recipe operations
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insert(recipe)

    suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)

    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)

    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getRecipeById(id)

    fun searchRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecipes(query)

    // Daily Goal operations
    suspend fun setDailyGoal(day: String, protein: Double, carbs: Double, fat: Double) {
        val goal = DailyGoal(day, protein, carbs, fat)
        dailyGoalDao.insertOrUpdate(goal)
    }

    suspend fun getDailyGoal(day: String): DailyGoal? = dailyGoalDao.getGoalForDay(day)

    // Meal Log operations
    suspend fun logMeal(recipeId: Long, servings: Double, date: String = getTodayDate()): Long {
        val log = MealLog(recipeId = recipeId, servingsConsumed = servings, date = date)
        return mealLogDao.insert(log)
    }

    suspend fun deleteMealLog(log: MealLog) = mealLogDao.delete(log)

    fun getMealLogsForDate(date: String): Flow<List<MealLog>> = mealLogDao.getLogsForDate(date)

    // Helper function to get today's date in "YYYY-MM-DD" format
    private fun getTodayDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    }

    // Advanced: Calculate today's macro totals
    suspend fun getTodaysMacroTotals(): Triple<Double, Double, Double> {
        val today = getTodayDate()
        val logs = mealLogDao.getLogsForDate(today)
        var totalProtein = 0.0
        var totalCarbs = 0.0
        var totalFat = 0.0

        logs.collect { mealLogs ->
            mealLogs.forEach { log ->
                val recipe = recipeDao.getRecipeById(log.recipeId)
                recipe?.let {
                    totalProtein += it.proteinPerServing * log.servingsConsumed
                    totalCarbs += it.carbsPerServing * log.servingsConsumed
                    totalFat += it.fatPerServing * log.servingsConsumed
                }
            }
        }

        return Triple(totalProtein, totalCarbs, totalFat)
    }
}
