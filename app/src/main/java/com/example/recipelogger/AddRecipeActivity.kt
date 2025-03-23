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

    // UI Components
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var ingredientsEditText: EditText
    private lateinit var prepTimeEditText: EditText
    private lateinit var cookTimeEditText: EditText
    private lateinit var servingsEditText: EditText
    private lateinit var instructionsEditText: EditText

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
            supportActionBar?.title = getString(R.string.add_recipe)

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

            // Set up Save button
            val saveButton = findViewById<Button>(R.id.button_save)
            saveButton.setOnClickListener {
                saveRecipe()
            }

            Log.d(TAG, "AddRecipeActivity onCreate - completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in AddRecipeActivity.onCreate", e)
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
                title = title,
                description = description,
                ingredients = ingredients,
                prepTime = prepTime,
                cookTime = cookTime,
                servings = servings,
                instructions = instructions
            )

            // Insert recipe into database
            recipeViewModel.insert(recipe) { newId ->
                Log.d(TAG, "Recipe inserted with ID: $newId")
            }

            // Show success message
            Toast.makeText(
                this,
                R.string.recipe_saved,
                Toast.LENGTH_SHORT
            ).show()

            // Go back to main screen
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