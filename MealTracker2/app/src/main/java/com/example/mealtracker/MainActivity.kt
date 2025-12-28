package com.example.mealtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mealtracker.ui.navigation.AppNavHost
import com.example.mealtracker.ui.navigation.Screen
import com.example.mealtracker.ui.theme.MealTrackerTheme
import android.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MealTrackerTheme {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        AppBottomNavigation(navController = navController)
                    }
                ) { paddingValues ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Only show bottom nav on main screens
    val showBottomNav = when (currentDestination?.route) {
        Screen.MealLog.route, Screen.RecipeList.route, Screen.DailyGoals.route -> true
        else -> false
    }

    if (showBottomNav) {
        NavigationBar {
            val items = listOf(
                Screen.MealLog to "Log Meals",
                Screen.RecipeList to "Recipes",
                Screen.DailyGoals to "Goals"
            )

            items.forEach { (screen, title) ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination to avoid building up a large stack
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                when (screen) {
                                    Screen.MealLog -> R.drawable.ic_menu_agenda
                                    Screen.RecipeList -> R.drawable.ic_menu_edit
                                    Screen.DailyGoals -> R.drawable.ic_menu_my_calendar
                                    else -> R.drawable.ic_menu_help
                                }
                            ),
                            contentDescription = title
                        )
                    },
                    label = { Text(title) }
                )
            }
        }
    }
}
