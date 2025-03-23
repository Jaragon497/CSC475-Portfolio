package com.example.recipelogger

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider

class AddRecipeActivity : AppCompatActivity() {

    private val TAG = "RecipeLogger"
    private lateinit var recipeViewModel: RecipeViewModel

    // Track if we're editing an existing recipe
    private var isEditMode = false
    private var recipeId = -1

    // UI Components
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var prepTimeEditText: EditText
    private lateinit var cookTimeEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var instructionsEditText: EditText

    companion object {
        const val EXTRA_RECIPE_ID = "com.example.recipelogger.RECIPE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Log.d(TAG, "AddRecipeActivity onCreate - starting")
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_add_recipe)

            // Set up toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            // Enable up button
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            // Check if we're editing an existing recipe
            recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, -1)
            isEditMode = recipeId != -1

            // Set title based on mode
            supportActionBar?.title = if (isEditMode) getString(R.string.edit_recipe) else getString(R.string.add_recipe)

            // Initialize UI components
            titleEditText = findViewById(R.id.edit_title)
            descriptionEditText = findViewById(R.id.edit_description)
            ingredientsEditText = findViewById(R.id.edit_ingredients)
            prepTimeEditText = findViewById(R.id.edit_prep_time)
            cookTimeEditText = findViewById(R.id.edit_cook_time)
            servingsEditText = findViewById(R.id.edit_servings)
            instructionsEditText = findViewById(R.id.edit_instructions)

            // Initialize ViewModel
            recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

            // If editing, load the existing recipe data
            if (isEditMode) {
                loadRecipeForEditing()
            }

            // Set up Save button
            val saveButton = findViewById<Button>(R.id.button_save)
            saveButton.text = if (isEditMode) getString(R.string.update_recipe) else getString(R.string.save_recipe)
            saveButton.setOnClickListener {
                saveRecipe()
            }

            Log.d(TAG, "AddRecipeActivity onCreate - completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in AddRecipeActivity.onCreate", e)
        }
    }

    private fun loadRecipeForEditing() {
        recipeViewModel.getRecipeById(recipeId).observe(this) { recipe ->
            if (recipe != null) {
                // Populate UI fields with recipe data
                titleEditText.setText(recipe.title)
                descriptionEditText.setText(recipe.description)
                prepTimeEditText.setText(recipe.prepTime.toString())
                cookTimeEditText.setText(recipe.cookTime.toString())
                servingsEditText.setText(recipe.servings.toString())

                // Convert ingredients list to string
                val ingredientsText = recipe.ingredients.joinToString("\n")
                ingredientsEditText.setText(ingredientsText)

                // Convert instructions list to string
                val instructionsText = recipe.instructions.joinToString("\n\n")
                instructionsEditText.setText(instructionsText)
            } else {
                Toast.makeText(this, R.string.recipe_not_found, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun saveRecipe() {
        try {
            // Get values from the form
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val ingredientsText = ingredientsEditText.text.toString().trim()
            val prepTimeText = prepTimeEditText.text.toString().trim()
            val cookTimeText = cookTimeEditText.text.toString().trim()
            val servingsText = servingsEditText.text.toString().trim()
            val instructionsText = instructionsEditText.text.toString().trim()

            // Validate input
            if (title.isEmpty() || ingredientsText.isEmpty() || instructionsText.isEmpty()) {
                Toast.makeText(
                    this,
                    R.string.empty_fields_message,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Parse ingredients (one per line)
            val ingredients = ingredientsText.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

            // Parse instructions (paragraphs are steps)
            val instructions = instructionsText.split("\n\n").map { it.trim() }.filter { it.isNotEmpty() }

            // Parse numeric values or use defaults
            val prepTime = prepTimeText.toIntOrNull() ?: 0
            val cookTime = cookTimeText.toIntOrNull() ?: 0
            val servings = servingsText.toIntOrNull() ?: 1

            // Create Recipe object
            val recipe = Recipe(
                id = if (isEditMode) recipeId else 0,
                title = title,
                description = description,
                ingredients = ingredients,
                prepTime = prepTime,
                cookTime = cookTime,
                servings = servings,
                instructions = instructions
            )

            // Update or insert recipe
            if (isEditMode) {
                // Update existing recipe
                recipeViewModel.update(recipe)
                Toast.makeText(
                    this,
                    R.string.recipe_updated,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Insert new recipe
                recipeViewModel.insert(recipe) { newId ->
                    Log.d(TAG, "Recipe inserted with ID: $newId")
                }
                Toast.makeText(
                    this,
                    R.string.recipe_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Go back to previous screen
            finish()

        } catch (e: Exception) {
            Log.e(TAG, "Error saving recipe", e)
            Toast.makeText(
                this,
                "Error saving recipe: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle back button in action bar
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}