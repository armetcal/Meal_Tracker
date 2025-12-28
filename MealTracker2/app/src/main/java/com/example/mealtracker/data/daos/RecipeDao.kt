package com.example.mealtracker.data.daos

import androidx.room.*
import com.example.mealtracker.data.entities.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: Recipe): Long

    @Update
    suspend fun update(recipe: Recipe)

    @Delete
    suspend fun delete(recipe: Recipe)

    @Query("SELECT * FROM recipes ORDER BY LOWER(name) ASC")  // Changed to LOWER for case-insensitive sorting
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Long): Recipe?

    @Query("SELECT * FROM recipes WHERE name LIKE :query ORDER BY LOWER(name) ASC")
    fun searchRecipes(query: String): Flow<List<Recipe>>
}

