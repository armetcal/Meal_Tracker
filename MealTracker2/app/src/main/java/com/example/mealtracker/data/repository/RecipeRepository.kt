package com.example.mealtracker.data.repository

import com.example.mealtracker.data.daos.DailyGoalDao
import com.example.mealtracker.data.daos.MealLogDao
import com.example.mealtracker.data.daos.RecipeDao
import com.example.mealtracker.data.entities.DailyGoal
import com.example.mealtracker.data.entities.MealLog
import com.example.mealtracker.data.entities.Recipe
import kotlinx.coroutines.flow.Flow

class RecipeRepository(
    private val recipeDao: RecipeDao,
    private val dailyGoalDao: DailyGoalDao,
    private val mealLogDao: MealLogDao
) {
    // Recipe methods
    suspend fun insertRecipe(recipe: Recipe) = recipeDao.insert(recipe)
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    suspend fun getRecipeById(id: Long): Recipe? = recipeDao.getRecipeById(id)

    // DailyGoal methods
    suspend fun insertGoal(goal: DailyGoal) = dailyGoalDao.insert(goal)
    suspend fun updateGoal(goal: DailyGoal) = dailyGoalDao.update(goal)
    suspend fun deleteGoal(goal: DailyGoal) = dailyGoalDao.delete(goal)
    fun getAllGoals(): Flow<List<DailyGoal>> = dailyGoalDao.getAllGoals()
    fun getGoalForDay(dayOfWeek: String): Flow<DailyGoal?> = dailyGoalDao.getGoalForDay(dayOfWeek)

    // MealLog methods
    suspend fun insertMealLog(mealLog: MealLog) = mealLogDao.insert(mealLog)
    suspend fun updateMealLog(mealLog: MealLog) = mealLogDao.update(mealLog)
    suspend fun deleteMealLog(mealLog: MealLog) = mealLogDao.delete(mealLog)
    fun getMealLogsForDate(date: String): Flow<List<MealLog>> =
        mealLogDao.getMealLogsForDate(date)
    fun getAllMealLogs(): Flow<List<MealLog>> = mealLogDao.getAllMealLogs()
}
