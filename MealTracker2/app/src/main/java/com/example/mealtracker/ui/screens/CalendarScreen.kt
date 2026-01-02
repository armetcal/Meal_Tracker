package com.example.mealtracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mealtracker.data.entities.MealLog
import com.example.mealtracker.data.entities.Recipe
import com.example.mealtracker.ui.viewmodels.RecipeViewModel
import com.example.mealtracker.ui.viewmodels.RecipeViewModelFactory
import android.R
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.interaction.MutableInteractionSource  // ADD THIS IMPORT


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit
) {
    val allMealLogs by viewModel.getAllMealLogs().collectAsState(emptyList())
    val recipes by viewModel.allRecipes.collectAsState(emptyList())
    val allGoals by viewModel.allGoals.collectAsState(emptyList())

    // Current month and year
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }

    // Verify data is loading
    LaunchedEffect(allMealLogs) {
        println("CalendarScreen: Loaded ${allMealLogs.size} meal logs")
        allMealLogs.take(5).forEach { log ->
            println("Meal Log: date=${log.date}, recipeId=${log.recipeId}, servings=${log.servingsConsumed}")
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Progress Calendar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.ic_menu_revert),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Month navigation
                    IconButton(onClick = {
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, -1)
                        currentMonth = newMonth
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_media_previous),
                            contentDescription = "Previous Month"
                        )
                    }
                    IconButton(onClick = {
                        val newMonth = currentMonth.clone() as Calendar
                        newMonth.add(Calendar.MONTH, 1)
                        currentMonth = newMonth
                    }) {
                        Icon(
                            painterResource(R.drawable.ic_media_next),
                            contentDescription = "Next Month"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Month header
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            // Stats summary
            val uniqueDates = allMealLogs.map { it.date }.distinct().size
            Text(
                text = "Tracking ${uniqueDates} days of meals",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            // Weekday headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            CalendarGrid(
                month = currentMonth,
                mealLogs = allMealLogs,
                recipes = recipes,
                goals = allGoals,
                onDateSelected = { date ->
                    println("Selected date: $date")
                }
            )

            // Legend
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Progress Legend:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem(color = Color(0xFF4CAF50), label = "80-100%")
                LegendItem(color = Color(0xFFFF9800), label = "50-79%")
                LegendItem(color = Color(0xFFF44336), label = "1-49%")
                LegendItem(color = Color.LightGray, label = "No data")
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CalendarGrid(
    month: Calendar,
    mealLogs: List<MealLog>,
    recipes: List<Recipe>,
    goals: List<com.example.mealtracker.data.entities.DailyGoal>,
    onDateSelected: (String) -> Unit
) {
    val daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfMonth = month.apply { set(Calendar.DAY_OF_MONTH, 1) }.get(Calendar.DAY_OF_WEEK)

    // Calculate progress for each day
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dailyProgress = remember(mealLogs, month) {
        (1..daysInMonth).associate { day ->
            val date = month.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, day)
            val dateString = dateFormat.format(date.time)

            val dayMealLogs = mealLogs.filter { it.date == dateString }
            val progress = calculateDailyProgress(dayMealLogs, recipes, goals, date)
            dateString to progress
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Empty cells for days before the first day of the month
        items((firstDayOfMonth - 1).coerceAtLeast(0)) {
            Box(modifier = Modifier.aspectRatio(1f))
        }

        // Days of the month
        items(daysInMonth) { day ->
            val date = month.clone() as Calendar
            date.set(Calendar.DAY_OF_MONTH, day + 1)
            val dateString = dateFormat.format(date.time)
            val progress = dailyProgress[dateString] ?: 0.0

            CalendarDay(
                day = day + 1,
                progress = progress,
                isToday = isToday(date),
                onClick = { onDateSelected(dateString) }
            )
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    progress: Double,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isToday) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    // Color based on progress
    val progressColor = when {
        progress >= 0.8 -> Color(0xFF4CAF50) // Green
        progress >= 0.5 -> Color(0xFFFF9800) // Orange
        progress > 0.0 -> Color(0xFFF44336)  // Red
        else -> Color.LightGray              // Gray for no data
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(backgroundColor, MaterialTheme.shapes.small)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )

            // Progress indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(progressColor, CircleShape)
            )
        }
    }
}

// Calculate daily progress (0.0 to 1.0)
private fun calculateDailyProgress(
    mealLogs: List<MealLog>,
    recipes: List<Recipe>,
    goals: List<com.example.mealtracker.data.entities.DailyGoal>,
    date: Calendar
): Double {
    if (mealLogs.isEmpty()) return 0.0

    val dayOfWeek = when (date.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        Calendar.SUNDAY -> "Sunday"
        else -> "Monday"
    }

    val goal = goals.find { it.dayOfWeek == dayOfWeek } ?:
    com.example.mealtracker.data.entities.DailyGoal(
        dayOfWeek = dayOfWeek,
        proteinGoal = 150.0,
        carbsGoal = 200.0,
        fatGoal = 50.0
    )

    val totals = calculateTodaysTotals(mealLogs, recipes)
    val caloriesGoal = goal.calorieGoal
    val caloriesActual = totals["calories"] ?: 0.0

    // Progress as % of goal, capped at 100%
    return (caloriesActual / caloriesGoal).coerceIn(0.0, 1.0)
}

private fun isToday(date: Calendar): Boolean {
    val today = Calendar.getInstance()
    return date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            date.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
            date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
}

private fun calculateTodaysTotals(mealLogs: List<MealLog>, recipes: List<Recipe>): Map<String, Double> {
    var protein = 0.0
    var carbs = 0.0
    var fat = 0.0
    var calories = 0.0

    mealLogs.forEach { mealLog ->
        val recipe = recipes.find { it.id == mealLog.recipeId }
        recipe?.let {
            protein += it.proteinPerServing * mealLog.servingsConsumed
            carbs += it.carbsPerServing * mealLog.servingsConsumed
            fat += it.fatPerServing * mealLog.servingsConsumed
            calories += it.caloriesPerServing * mealLog.servingsConsumed
        }
    }

    return mapOf(
        "protein" to protein,
        "carbs" to carbs,
        "fat" to fat,
        "calories" to calories
    )
}
