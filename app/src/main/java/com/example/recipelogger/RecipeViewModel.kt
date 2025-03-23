package com.example.recipelogger

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for Recipe data.
 * Provides data to the UI and survives configuration changes.
 */
class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository

    val allRecipes: LiveData<List<Recipe>>

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        allRecipes = repository.allRecipes
    }

    fun insert(recipe: Recipe, callback: (Long) -> Unit = {}) = viewModelScope.launch(Dispatchers.IO) {
        val id = repository.insert(recipe)
        launch(Dispatchers.Main) {
            callback(id)
        }
    }

    fun update(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(recipe)
    }

    fun delete(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(recipe)
    }

    fun getRecipeById(id: Int): LiveData<Recipe> {
        return repository.getRecipeById(id)
    }
}