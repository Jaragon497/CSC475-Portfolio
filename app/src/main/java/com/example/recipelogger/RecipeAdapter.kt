package com.example.recipelogger

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.recipe_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.recipe_description)
        val cardView: CardView = itemView.findViewById(R.id.recipe_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.titleTextView.text = recipe.title

        // Truncate description if it's too long
        val maxDescriptionLength = 100
        holder.descriptionTextView.text = if (recipe.description.length > maxDescriptionLength) {
            recipe.description.substring(0, maxDescriptionLength - 3) + "..."
        } else {
            recipe.description
        }

        holder.cardView.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount() = recipes.size
}
