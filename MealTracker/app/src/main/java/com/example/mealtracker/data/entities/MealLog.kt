package com.example.mealtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipeId: Long,
    val servingsConsumed: Double,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
)
