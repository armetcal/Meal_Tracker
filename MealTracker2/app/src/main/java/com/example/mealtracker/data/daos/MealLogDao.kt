package com.example.mealtracker.data.daos

import androidx.room.*
import com.example.mealtracker.data.entities.MealLog
import kotlinx.coroutines.flow.Flow

@Dao
interface MealLogDao {
    @Insert
    suspend fun insert(mealLog: MealLog): Long

    @Update
    suspend fun update(mealLog: MealLog)

    @Delete
    suspend fun delete(mealLog: MealLog)

    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getMealLogsForDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY date DESC, timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLog>>
}
