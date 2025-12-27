package com.example.mealtracker.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.mealtracker.data.daos.RecipeDao
import com.example.mealtracker.data.daos.DailyGoalDao
import com.example.mealtracker.data.daos.MealLogDao
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.data.entities.DailyGoal
import com.example.mealtracker.data.entities.MealLog

@Database(
    entities = [
        com.example.mealtracker.data.entities.Recipe::class,
        com.example.mealtracker.data.entities.DailyGoal::class,
        com.example.mealtracker.data.entities.MealLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MacroDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun dailyGoalDao(): DailyGoalDao
    abstract fun mealLogDao(): MealLogDao

    companion object {
        @Volatile
        private var INSTANCE: MacroDatabase? = null

        fun getDatabase(context: Context): MacroDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MacroDatabase::class.java,
                    "macro_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
