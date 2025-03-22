package com.example.recipelogger

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val prepTime: Int, // in minutes
    val cookTime: Int, // in minutes
    val servings: Int,
    val instructions: List<String>
)