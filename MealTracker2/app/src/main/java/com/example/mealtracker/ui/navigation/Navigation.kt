package com.example.mealtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mealtracker.ui.screens.*

sealed class Screen(val route: String) {
    object MealLog : Screen("meal_log")
    object RecipeList : Screen("recipes")
    object AddRecipe : Screen("add_recipe")
    object DailyGoals : Screen("daily_goals")
    object EditRecipe : Screen("edit_recipe")
    object Calendar : Screen("calendar")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.MealLog.route,
        modifier = modifier
    ) {
        composable(Screen.MealLog.route) {
            MealLogScreen(
                onBack = { /* No back needed on home screen */ }
            )
        }
        composable(Screen.RecipeList.route) {
            RecipeListScreen(
                onAddRecipe = { navController.navigate(Screen.AddRecipe.route) },
                onEditRecipe = { recipeId ->
                    navController.navigate("${Screen.EditRecipe.route}/$recipeId")
                }
            )
        }
        composable(Screen.AddRecipe.route) {
            AddRecipeScreen(
                onBack = { navController.popBackStack() },
                onRecipeAdded = { navController.popBackStack() }
            )
        }
        composable(Screen.DailyGoals.route) {
            DailyGoalScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "${Screen.EditRecipe.route}/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            println("Navigation: Editing recipe with ID: $recipeId")  // Debug logging
            EditRecipeScreen(
                recipeId = recipeId,
                onBack = { navController.popBackStack() },
                onRecipeUpdated = { navController.popBackStack() }
            )
        }

        composable(Screen.Calendar.route) {
            CalendarScreen(
                onBack = { navController.popBackStack() }
            )
        }

    }
}
