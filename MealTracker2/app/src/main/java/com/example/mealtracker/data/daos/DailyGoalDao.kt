package com.example.mealtracker.data.daos

import androidx.room.*
import com.example.mealtracker.data.entities.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert
    suspend fun insert(goal: DailyGoal)

    @Update
    suspend fun update(goal: DailyGoal)

    @Delete
    suspend fun delete(goal: DailyGoal)

    @Query("SELECT * FROM daily_goals WHERE dayOfWeek = :dayOfWeek")
    fun getGoalForDay(dayOfWeek: String): Flow<DailyGoal?>

    @Query("SELECT * FROM daily_goals ORDER BY " +
            "CASE dayOfWeek " +
            "WHEN 'Monday' THEN 1 " +
            "WHEN 'Tuesday' THEN 2 " +
            "WHEN 'Wednesday' THEN 3 " +
            "WHEN 'Thursday' THEN 4 " +
            "WHEN 'Friday' THEN 5 " +
            "WHEN 'Saturday' THEN 6 " +
            "WHEN 'Sunday' THEN 7 " +
            "END ASC")
    fun getAllGoals(): Flow<List<DailyGoal>>
}
