package com.example.mealtracker.data.daos

import androidx.room.*
import com.example.mealtracker.data.entities.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Insert
    suspend fun insert(log: MealLog): Long

    @Delete
    suspend fun delete(log: MealLog)

    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getLogsForDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE recipeId = :recipeId")
    suspend fun getLogsForRecipe(recipeId: Long): List<MealLog>

    @Query("DELETE FROM meal_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)
}
