package com.example.mealtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey
    val dayOfWeek: String,
    val proteinGoal: Double,
    val carbsGoal: Double,
    val fatGoal: Double
) {
    val calorieGoal: Double
        get() = (proteinGoal * 4) + (carbsGoal * 4) + (fatGoal * 9)
}
