package com.example.recipelogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider

class RecipeDetailActivity : AppCompatActivity() {

    private val TAG = "RecipeLogger"
    private lateinit var recipeViewModel: RecipeViewModel
    private var currentRecipe: Recipe? = null
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Log.d(TAG, "DetailActivity onCreate - starting")
            super.onCreate(savedInstanceState)
            Log.d(TAG, "DetailActivity onCreate - setting content view")
            setContentView(R.layout.activity_recipe_detail)

            // Set up toolbar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            Log.d(TAG, "DetailActivity onCreate - setting up action bar")
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)

            // Get recipe ID from intent
            recipeId = intent.getIntExtra(MainActivity.EXTRA_RECIPE_ID, -1)
            Log.d(TAG, "DetailActivity onCreate - got recipe ID: $recipeId")

            if (recipeId == -1) {
                Log.e(TAG, "DetailActivity onCreate - invalid recipe ID")
                finish()
                return
            }

            // Set up the ViewModel
            recipeViewModel = ViewModelProvider(this).get(RecipeViewModel::class.java)

            // Observe the recipe
            recipeViewModel.getRecipeById(recipeId).observe(this) { recipe ->
                if (recipe != null) {
                    Log.d(TAG, "DetailActivity - observed recipe: ${recipe.title}")
                    // Save the current recipe
                    currentRecipe = recipe

                    // Set title in the toolbar
                    supportActionBar?.title = recipe.title

                    // Display the recipe details
                    displayRecipe(recipe)
                } else {
                    Log.e(TAG, "DetailActivity - recipe not found for ID: $recipeId")
                    finish()
                }
            }

            Log.d(TAG, "DetailActivity onCreate - completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in DetailActivity.onCreate", e)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Handle back button in action bar
                onBackPressed()
                true
            }
            R.id.action_edit -> {
                // Open edit screen with current recipe
                currentRecipe?.let { recipe ->
                    val intent = Intent(this, AddRecipeActivity::class.java)
                    intent.putExtra(AddRecipeActivity.EXTRA_RECIPE_ID, recipe.id)
                    startActivity(intent)
                }
                true
            }
            R.id.action_delete -> {
                // Show confirmation dialog before deleting
                confirmDeleteRecipe()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmDeleteRecipe() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_confirmation_title)
            .setMessage(R.string.delete_confirmation_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteRecipe()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteRecipe() {
        currentRecipe?.let { recipe ->
            recipeViewModel.delete(recipe)
            Toast.makeText(this, R.string.recipe_deleted, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayRecipe(recipe: Recipe) {
        try {
            // Find views
            Log.d(TAG, "displayRecipe - finding views")
            val titleTextView = findViewById<TextView>(R.id.detail_title)
            val descriptionTextView = findViewById<TextView>(R.id.detail_description)
            val prepTimeTextView = findViewById<TextView>(R.id.detail_prep_time)
            val cookTimeTextView = findViewById<TextView>(R.id.detail_cook_time)
            val servingsTextView = findViewById<TextView>(R.id.detail_servings)
            val ingredientsTextView = findViewById<TextView>(R.id.detail_ingredients)
            val instructionsTextView = findViewById<TextView>(R.id.detail_instructions)

            // Populate views with recipe data
            Log.d(TAG, "displayRecipe - setting data")
            titleTextView.text = recipe.title
            descriptionTextView.text = recipe.description
            prepTimeTextView.text = getString(R.string.prep_time_format, recipe.prepTime)
            cookTimeTextView.text = getString(R.string.cook_time_format, recipe.cookTime)
            servingsTextView.text = getString(R.string.servings_format, recipe.servings)

            // Format ingredients as bullet points
            val ingredientsText = recipe.ingredients.joinToString("\n• ", "• ")
            ingredientsTextView.text = ingredientsText

            // Format instructions as numbered steps
            val instructionsText = recipe.instructions.mapIndexed { index, step ->
                "${index + 1}. $step"
            }.joinToString("\n\n")
            instructionsTextView.text = instructionsText
            Log.d(TAG, "displayRecipe - completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in displayRecipe", e)
        }
    }
}