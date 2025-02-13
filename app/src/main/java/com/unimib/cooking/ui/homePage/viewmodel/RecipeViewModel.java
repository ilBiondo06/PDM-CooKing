package com.unimib.cooking.ui.homePage.viewmodel;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.Result;
import com.unimib.cooking.repository.recipe.RecipeRepository;

import java.util.List;

public class RecipeViewModel extends ViewModel {

    private static final String TAG = RecipeViewModel.class.getSimpleName();

    private final RecipeRepository recipeRepository;
    private final int page;
    private MutableLiveData<Result> recipesListLiveData;
    private MutableLiveData<Result> randomRecipeLiveData;
    private MutableLiveData<Result> recipeByIdLiveData;
    private MutableLiveData<Result> favoriteRecipesListLiveData;
    private boolean isFirstLaunch = true;

    public RecipeViewModel(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
        this.page = 1;
    }

    public MutableLiveData<Result> getRecipes(String letter, long lastUpdate) {
        //if (recipesListLiveData == null) {
            fetchRecipes(letter, lastUpdate);
        //}
        return recipesListLiveData;
    }

    public void insertRecipe(Recipe recipe) {
        recipeRepository.insertRecipe(recipe);
    }

    public void insertRecipes(List<Recipe> recipes) {
        recipeRepository.insertRecipes(recipes);
    }

    public MutableLiveData<Result> getRandomRecipe(long lastUpdate) {

        fetchRandomRecipe(lastUpdate);

        Log.d(TAG, "getRandomRecipe: " + randomRecipeLiveData);
        return randomRecipeLiveData;
    }

    public MutableLiveData<Result> getRecipesByName(String name,long lastUpdate) {

        fetchRecipesByName(name, lastUpdate);

        Log.d(TAG, "getRecipesByName: " + randomRecipeLiveData);
        return recipesListLiveData;
    }

    public MutableLiveData<Result> getRecipeByArea(String area, long lastUpdate) {

        fetchRecipesByArea(area, lastUpdate);

        Log.d(TAG, "getRecipeByArea: " + recipesListLiveData);
        return recipesListLiveData;
    }


    private void fetchRecipesByName(String name, long lastUpdate) {
        recipesListLiveData = recipeRepository.fetchRecipesByName(name, page, lastUpdate);
    }


    private void fetchRecipesByArea(String area, long lastUpdate) {
        recipesListLiveData = recipeRepository.fetchRecipesByArea(area, page, lastUpdate);
    }

    public MutableLiveData<Result> getRecipeById(long id, long lastUpdate) {
        fetchRecipeById(id, lastUpdate);
        return recipeByIdLiveData;
    }

    public void fetchRecipeById(long id, long lastUpdate) {
        recipeByIdLiveData = recipeRepository.fetchRecipeById(id, page, lastUpdate);
    }

    public MutableLiveData<Result> getRecipeByCategory(String categoria, long lastUpdate) {

        fetchRecipesByCategory(categoria, lastUpdate);


        return recipesListLiveData;
    }


    private void fetchRecipesByCategory(String categoria, long lastUpdate) {
        recipesListLiveData = recipeRepository.fetchRecipesByCategory(categoria, page, lastUpdate);
    }

    private void fetchRecipes(String letter, long lastUpdate) {
        recipesListLiveData = recipeRepository.fetchRecipesByLetter(letter, page, lastUpdate);
    }

    private void fetchRandomRecipe(long lastUpdate) {
        randomRecipeLiveData = recipeRepository.fetchRandomRecipe(page, lastUpdate);
    }


    public MutableLiveData<Result> getFavoriteRecipesLiveData() {
        if (favoriteRecipesListLiveData == null) {
            getFavoriteRecipes();
        }
        return favoriteRecipesListLiveData;
    }


    public void updateRecipe(Recipe recipe) {
        recipeRepository.updateRecipe(recipe);
    }



    private void getFavoriteRecipes() {
        favoriteRecipesListLiveData = recipeRepository.getFavoriteRecipes();
    }

    public void removeFromFavorite(Recipe recipe) {
        recipeRepository.removeFromFavorites(recipe);

        // Aggiorna i preferiti dopo la rimozione
        getFavoriteRecipes();
    }

    public void deleteAllFavoriteRecipes() {
        recipeRepository.deleteFavoriteRecipes();
        favoriteRecipesListLiveData = null;
    }

    //aggiornare lista di ricette
    public MutableLiveData<Result> updateRecipes(String letter, long lastUpdate) {
        fetchRecipes(letter, lastUpdate);
        return recipesListLiveData;
    }


}
