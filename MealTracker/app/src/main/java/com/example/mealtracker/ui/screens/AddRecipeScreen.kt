@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    viewModel: RecipeViewModel = viewModel(factory = RecipeViewModelFactory(LocalContext.current)),
    onBack: () -> Unit,
    onRecipeAdded: () -> Unit
) {
    var recipeName by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") } // NEW: Calorie input
    var servingSize by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_revert),
                            contentDescription = "Back"
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error message
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Recipe name
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Recipe Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Protein
            OutlinedTextField(
                value = protein,
                onValueChange = { protein = it },
                label = { Text("Protein (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Carbs
            OutlinedTextField(
                value = carbs,
                onValueChange = { carbs = it },
                label = { Text("Carbs (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Fat
            OutlinedTextField(
                value = fat,
                onValueChange = { fat = it },
                label = { Text("Fat (grams per serving)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // NEW: Calories input
            OutlinedTextField(
                value = calories,
                onValueChange = { calories = it },
                label = { Text("Calories per serving") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Serving size
            OutlinedTextField(
                value = servingSize,
                onValueChange = { servingSize = it },
                label = { Text("Serving Size (e.g., '1 cup', '100g')") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Add button
            Button(
                onClick = {
                    if (recipeName.isBlank()) {
                        errorMessage = "Please enter a recipe name"
                        return@Button
                    }

                    val newRecipe = Recipe(
                        name = recipeName,
                        proteinPerServing = protein.toDoubleOrNull() ?: 0.0,
                        carbsPerServing = carbs.toDoubleOrNull() ?: 0.0,
                        fatPerServing = fat.toDoubleOrNull() ?: 0.0,
                        servingSize = if (servingSize.isBlank()) "1 serving" else servingSize
                    )
                    // Note: We'll need to update the Recipe entity to store calories

                    viewModel.insertRecipe(newRecipe)
                    onRecipeAdded()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Recipe")
            }
        }
    }
}
