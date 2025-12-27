package com.example.mealtracker.data.daos

import androidx.room.*
import com.example.mealtracker.data.entities.DailyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(goal: DailyGoal)

    @Query("SELECT * FROM daily_goals WHERE dayOfWeek = :day")
    suspend fun getGoalForDay(day: String): DailyGoal?

    @Query("SELECT * FROM daily_goals")
    fun getAllGoals(): Flow<List<DailyGoal>>

    @Query("DELETE FROM daily_goals WHERE dayOfWeek = :day")
    suspend fun deleteGoalForDay(day: String)
}
