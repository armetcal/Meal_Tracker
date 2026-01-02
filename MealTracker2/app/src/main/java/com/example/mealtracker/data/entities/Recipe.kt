package com.example.mealtracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val proteinPerServing: Double,
    val carbsPerServing: Double,
    val fatPerServing: Double,
    val servingSize: String,
    val imagePath: String? = null, // Path to optional photo - finish later
    val createdAt: Long = System.currentTimeMillis()
) {
    val caloriesPerServing: Double
        get() = (proteinPerServing * 4) + (carbsPerServing * 4) + (fatPerServing * 9)
}
