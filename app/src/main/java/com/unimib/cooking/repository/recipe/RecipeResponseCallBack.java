package com.unimib.cooking.repository.recipe;

import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.model.RecipeAPIResponse;

import java.util.List;

public interface RecipeResponseCallBack {

    void onSuccessFromRemote(RecipeAPIResponse recipeAPIResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Recipe> recipesList);
    void onFailureFromLocal(Exception exception);
    void onRecipesFavoriteStatusChanged(Recipe recipe, List<Recipe> favoriteRecipes);
    void onRecipesFavoriteStatusChanged(List<Recipe> recipes);
    void onDeleteFavoriteRecipesSuccess(List<Recipe> favoriteRecipes);
    void onSuccessFromLocalByLetter(List<Recipe> recipesList);
    void onSuccessFromLocalByCategory(List<Recipe> recipesList);
    void onSuccessFromLocalByArea(List<Recipe> recipesList);

}
