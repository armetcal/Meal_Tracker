package com.example.mealtracker.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mealtracker.data.database.MacroDatabase
import com.example.mealtracker.data.repository.RecipeRepository

class RecipeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            val database = MacroDatabase.getDatabase(context)
            val repository = RecipeRepository(
                recipeDao = database.recipeDao(),
                mealLogDao = database.mealLogDao(),
                dailyGoalDao = database.dailyGoalDao()
            )
            return RecipeViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
