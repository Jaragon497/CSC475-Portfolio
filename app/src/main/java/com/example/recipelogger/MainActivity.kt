package com.example.recipelogger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val TAG = "RecipeLogger"

    // Move sample recipes to companion object so they can be accessed statically
    companion object {
        const val EXTRA_RECIPE_ID = "com.example.recipelogger.RECIPE_ID"

        val sampleRecipes = listOf(
            Recipe(
                id = 1,
                title = "Spaghetti Carbonara",
                description = "A classic Italian pasta dish with eggs, cheese, bacon, and black pepper.",
                ingredients = listOf(
                    "200g spaghetti",
                    "100g pancetta or bacon",
                    "2 large eggs",
                    "50g Pecorino Romano cheese, grated",
                    "50g Parmesan cheese, grated",
                    "Freshly ground black pepper",
                    "Salt to taste"
                ),
                prepTime = 10,
                cookTime = 15,
                servings = 2,
                instructions = listOf(
                    "Bring a large pot of salted water to boil and cook spaghetti according to package directions.",
                    "While pasta is cooking, heat a large skillet and cook pancetta until crispy.",
                    "In a bowl, whisk eggs and mix in the grated cheeses and pepper.",
                    "Drain pasta, reserving a little cooking water. Immediately add hot pasta to the skillet with pancetta.",
                    "Remove from heat, and quickly stir in the egg and cheese mixture, thinning with pasta water if needed.",
                    "The heat from the pasta will cook the eggs and create a creamy sauce. Serve immediately with extra cheese and black pepper."
                )
            ),
            Recipe(
                id = 2,
                title = "Chicken Stir Fry",
                description = "A quick and healthy meal with chicken and vegetables.",
                ingredients = listOf(
                    "500g chicken breast, sliced",
                    "1 bell pepper, sliced",
                    "1 carrot, julienned",
                    "1 broccoli head, cut into florets",
                    "3 tbsp soy sauce",
                    "1 tbsp honey",
                    "2 cloves garlic, minced",
                    "1 tbsp vegetable oil"
                ),
                prepTime = 15,
                cookTime = 10,
                servings = 4,
                instructions = listOf(
                    "Heat oil in a wok or large frying pan over high heat.",
                    "Add chicken and stir fry until almost cooked through, about 3-4 minutes.",
                    "Add vegetables and stir fry for another 3-4 minutes until vegetables are tender-crisp.",
                    "Mix soy sauce, honey, and garlic in a small bowl, then pour over the chicken and vegetables.",
                    "Stir fry for another minute until everything is well coated and heated through.",
                    "Serve hot with rice or noodles."
                )
            ),
            Recipe(
                id = 3,
                title = "Berry Smoothie",
                description = "A refreshing and nutritious smoothie packed with berries and yogurt.",
                ingredients = listOf(
                    "1 cup mixed berries (strawberries, blueberries, raspberries)",
                    "1 banana",
                    "1/2 cup Greek yogurt",
                    "1/2 cup milk or almond milk",
                    "1 tbsp honey or maple syrup (optional)",
                    "Ice cubes"
                ),
                prepTime = 5,
                cookTime = 0,
                servings = 1,
                instructions = listOf(
                    "Add all ingredients to a blender.",
                    "Blend until smooth and creamy.",
                    "Pour into a glass and enjoy immediately."
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            Log.d(TAG, "MainActivity onCreate - starting")
            super.onCreate(savedInstanceState)
            Log.d(TAG, "MainActivity onCreate - setting content view")
            setContentView(R.layout.activity_main)

            // Set up the toolbar as the action bar
            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)

            Log.d(TAG, "MainActivity onCreate - finding RecyclerView")
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)

            Log.d(TAG, "MainActivity onCreate - setting LayoutManager")
            recyclerView.layoutManager = GridLayoutManager(this, 2) // 2 columns grid

            Log.d(TAG, "MainActivity onCreate - creating adapter")
            val adapter = RecipeAdapter(sampleRecipes) { recipe ->
                // Navigate to detail screen
                Log.d(TAG, "Clicked on recipe: ${recipe.title}")
                val intent = Intent(this, RecipeDetailActivity::class.java)
                intent.putExtra(EXTRA_RECIPE_ID, recipe.id)
                startActivity(intent)
            }

            Log.d(TAG, "MainActivity onCreate - setting adapter")
            recyclerView.adapter = adapter
            Log.d(TAG, "MainActivity onCreate - completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in MainActivity.onCreate", e)
        }
    }
}