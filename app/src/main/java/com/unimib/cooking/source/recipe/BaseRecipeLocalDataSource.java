package com.unimib.cooking.source.recipe;

import com.unimib.cooking.model.Recipe;
import com.unimib.cooking.repository.recipe.RecipeResponseCallBack;

import java.util.List;

public abstract class BaseRecipeLocalDataSource {

    protected RecipeResponseCallBack recipeCallback;

    public void setRecipeCallback(RecipeResponseCallBack recipeCallback) {
        this.recipeCallback = recipeCallback;
    }

    public abstract void getFavoriteRecipes();

    public abstract void updateRecipe(Recipe recipe);

    public abstract void deleteFavoriteRecipes();

    public abstract void insertRecipes(List<Recipe> recipesList);

    public abstract void getRecipesByLetter(String letter);

    public abstract void getRecipesByName(String name);

    public abstract void getRecipesByCategory(String category);

    public abstract void getRecipesByArea(String area);

    public abstract void updateRecipeLikeStatus(Recipe recipe);

    public abstract void getRandomRecipe();

    public abstract void getRecipeById(long id);

    public void insertFavoriteRecipes(List<Recipe> recipes) {

    }
}
