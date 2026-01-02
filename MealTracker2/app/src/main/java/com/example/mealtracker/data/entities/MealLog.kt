package com.example.mealtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val servingsConsumed: Double,
    val date: String, // Format: "2024-01-15"
    val timestamp: Long = System.currentTimeMillis()
)
